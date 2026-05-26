const fs = require("fs");
const path = require("path");

const SCRIPT_DIR = __dirname;
const SECONDARY_DEV_ROOT = path.resolve(SCRIPT_DIR, "..");
const REPO_ROOT = path.resolve(SECONDARY_DEV_ROOT, "..");
const HEADER_ROOT = path.join(REPO_ROOT, "TessInterfaces");
const DOC_ROOT = path.join(REPO_ROOT, "tessng-secondary-doc", "public", "document", "V4.x");

const args = process.argv.slice(2);
const strictMode = args.includes("--strict");
const interfaceFilter = getArgValue("--interface");
const languageFilter = getArgValue("--language");

const TARGETS = [
  { language: "C++", docPath: path.join(DOC_ROOT, "C++", "InterfaceDetails.md"), compareExactSignature: true },
  { language: "Python3", docPath: path.join(DOC_ROOT, "Python3", "InterfaceDetails.md"), compareExactSignature: false },
  { language: "Java", docPath: path.join(DOC_ROOT, "Java", "InterfaceDetails.md"), compareExactSignature: false },
];

function getArgValue(name) {
  const index = args.indexOf(name);
  return index >= 0 && index + 1 < args.length ? args[index + 1] : "";
}

function normalizeSignature(signature) {
  return signature
    .replace(/[\u200B\uFEFF]/g, "")
    .replace(/\\([<>*])/g, "$1")
    .replace(/，/g, ",")
    .replace(/：/g, ":")
    .replace(/\s+/g, " ")
    .replace(/\s*([*&,()<>:=])\s*/g, "$1")
    .replace(/;$/, "")
    .trim();
}

function methodKey(signature) {
  const match = signature.match(/([A-Za-z_][A-Za-z0-9_]*)\((.*)\)\s*(?:const\s*)?(?:override\s*)?(?:noexcept\s*)?$/);
  if (!match) {
    return signature;
  }
  const methodName = match[1];
  const params = splitTopLevel(match[2], ",").filter(Boolean);
  return `${methodName}/${params.length}`;
}

function methodName(signature) {
  const match = signature.match(/([A-Za-z_][A-Za-z0-9_]*)\(/);
  return match ? match[1] : signature;
}

function splitTopLevel(text, separator) {
  const parts = [];
  let depthAngle = 0;
  let depthParen = 0;
  let depthBracket = 0;
  let current = "";
  for (const char of text) {
    if (char === "<") {
      depthAngle += 1;
    } else if (char === ">") {
      depthAngle = Math.max(0, depthAngle - 1);
    } else if (char === "(") {
      depthParen += 1;
    } else if (char === ")") {
      depthParen = Math.max(0, depthParen - 1);
    } else if (char === "[") {
      depthBracket += 1;
    } else if (char === "]") {
      depthBracket = Math.max(0, depthBracket - 1);
    }
    if (char === separator && depthAngle === 0 && depthParen === 0 && depthBracket === 0) {
      parts.push(current.trim());
      current = "";
      continue;
    }
    current += char;
  }
  if (current.trim()) {
    parts.push(current.trim());
  }
  return parts;
}

function stripInlineComment(line) {
  return line.replace(/\/\/.*$/, "").trim();
}

function cleanComment(comment) {
  return comment
    .replace(/^\/\*+/, "")
    .replace(/\*+\/$/, "")
    .replace(/^\*+/, "")
    .trim();
}

function collectHeaderFiles(dirPath) {
  const files = [];
  for (const entry of fs.readdirSync(dirPath, { withFileTypes: true })) {
    const fullPath = path.join(dirPath, entry.name);
    if (entry.isDirectory()) {
      files.push(...collectHeaderFiles(fullPath));
      continue;
    }
    if (entry.name.endsWith(".h")) {
      files.push(fullPath);
    }
  }
  return files;
}

function parseHeader(headerPath) {
  const className = path.basename(headerPath, ".h");
  const rawText = fs.readFileSync(headerPath, "utf8");
  const classMatch = rawText.match(new RegExp(`class\\s+(?:\\w+\\s+)?${className}\\s*(?::([\\s\\S]*?))?\\{`));
  const bases = [];
  if (classMatch && classMatch[1]) {
    for (const part of classMatch[1].split(",")) {
      const match = part.match(/\b(I[A-Za-z0-9_]+)\b/);
      if (match) {
        bases.push(match[1]);
      }
    }
  }

  const methods = [];
  const lines = rawText.split(/\r?\n/);
  let pendingComments = [];
  let buffer = "";
  let collecting = false;
  let methodComments = [];

  for (const rawLine of lines) {
    const trimmed = rawLine.trim();
    if (!collecting) {
      if (trimmed.startsWith("//")) {
        pendingComments.push(cleanComment(trimmed.slice(2)));
        continue;
      }
      if (trimmed.startsWith("/*") && trimmed.endsWith("*/")) {
        pendingComments.push(cleanComment(trimmed));
        continue;
      }
      if (!trimmed) {
        pendingComments = [];
        continue;
      }
      if (!trimmed.startsWith("virtual ")) {
        pendingComments = [];
        continue;
      }

      collecting = true;
      methodComments = pendingComments.slice();
      pendingComments = [];
      buffer = stripInlineComment(trimmed);
      const inlineCommentMatch = trimmed.match(/\/\/(.*)$/);
      if (inlineCommentMatch) {
        methodComments.push(cleanComment(inlineCommentMatch[1]));
      }
    } else {
      buffer += ` ${stripInlineComment(trimmed)}`;
    }

    if (!trimmed.includes(";") && !trimmed.includes("{")) {
      continue;
    }

    const signature = buffer
      .replace(/^virtual\s+/, "")
      .replace(/\{[\s\S]*$/, "")
      .replace(/\s*=\s*0\s*;/, ";")
      .replace(/;$/, "")
      .trim();
    if (signature.includes("(") && !signature.includes("~")) {
      methods.push({
        signature: normalizeSignature(signature),
        comment: methodComments.filter(Boolean).join(" ").trim(),
      });
    }
    buffer = "";
    collecting = false;
    methodComments = [];
  }

  return {
    bases,
    methods,
  };
}

function extractSections(docContent) {
  const headingRegex = /^####\s+.*?[（(](I[A-Za-z0-9_]+)[）)]\s*$/gm;
  const sections = [];
  let match;
  while ((match = headingRegex.exec(docContent))) {
    sections.push({ interface: match[1], start: match.index });
  }
  for (let index = 0; index < sections.length; index += 1) {
    const nextStart = index + 1 < sections.length ? sections[index + 1].start : docContent.length;
    sections[index].content = docContent.slice(sections[index].start, nextStart);
  }
  return sections;
}

function buildDocMethodKey(signature, language) {
  if (language !== "Python3") {
    return methodKey(signature);
  }

  const match = signature.match(/def\s+([A-Za-z_][A-Za-z0-9_]*)\((.*)\)\s*->/);
  if (!match) {
    return signature;
  }
  const params = splitTopLevel(match[2], ",").filter(Boolean);
  const filtered = params.filter(param => param.trim() !== "self");
  return `${match[1]}/${filtered.length}`;
}

function extractDocMethods(sectionContent, language) {
  return sectionContent
    .split(/\r?\n/)
    .map(line => line.trim())
    .filter(line => line.startsWith("**") && line.endsWith("**") && line.includes("("))
    .map(line => {
      const signature = normalizeSignature(line.slice(2, -2));
      return {
        signature,
        key: buildDocMethodKey(signature, language),
        name: methodName(signature),
      };
    });
}

function collectAllMethods(interfaceName, headerMap, parseCache, allCache, visiting = new Set()) {
  if (allCache.has(interfaceName)) {
    return allCache.get(interfaceName);
  }
  const headerPath = headerMap.get(interfaceName);
  if (!headerPath || visiting.has(interfaceName)) {
    return [];
  }

  visiting.add(interfaceName);
  let parsed = parseCache.get(interfaceName);
  if (!parsed) {
    parsed = parseHeader(headerPath);
    parseCache.set(interfaceName, parsed);
  }

  const methods = [...parsed.methods];
  for (const base of parsed.bases) {
    methods.push(...collectAllMethods(base, headerMap, parseCache, allCache, visiting));
  }

  const unique = dedupeMethods(methods);
  allCache.set(interfaceName, unique);
  visiting.delete(interfaceName);
  return unique;
}

function dedupeMethods(methods) {
  const seen = new Set();
  const unique = [];
  for (const method of methods) {
    if (seen.has(method.signature)) {
      continue;
    }
    seen.add(method.signature);
    unique.push(method);
  }
  return unique;
}

function findInsertionHints(directMethods, directKeysInDoc, targetMethod) {
  const index = directMethods.findIndex(method => method.signature === targetMethod.signature);
  let insertAfter = null;
  let insertBefore = null;

  for (let i = index - 1; i >= 0; i -= 1) {
    if (directKeysInDoc.has(methodKey(directMethods[i].signature))) {
      insertAfter = methodName(directMethods[i].signature);
      break;
    }
  }
  for (let i = index + 1; i < directMethods.length; i += 1) {
    if (directKeysInDoc.has(methodKey(directMethods[i].signature))) {
      insertBefore = methodName(directMethods[i].signature);
      break;
    }
  }

  return { insertAfter, insertBefore };
}

function buildInterfaceResult(target, section, directMethods, allMethods, docMethods) {
  const docKeys = new Set(docMethods.map(method => method.key));
  const directKeys = new Set(directMethods.map(method => methodKey(method.signature)));
  const allKeys = new Map(allMethods.map(method => [methodKey(method.signature), method]));
  const directKeysInDoc = new Set();
  for (const method of directMethods) {
    const key = methodKey(method.signature);
    if (docKeys.has(key)) {
      directKeysInDoc.add(key);
    }
  }

  const add = [];
  for (const method of directMethods) {
    const key = methodKey(method.signature);
    if (docKeys.has(key)) {
      continue;
    }
    const hints = findInsertionHints(directMethods, directKeysInDoc, method);
    add.push({
      method: methodName(method.signature),
      cppSignature: method.signature,
      comment: method.comment,
      insertAfter: hints.insertAfter,
      insertBefore: hints.insertBefore,
    });
  }

  const remove = [];
  for (const method of docMethods) {
    if (!allKeys.has(method.key)) {
      remove.push({
        method: method.name,
        documentedSignature: method.signature,
      });
    }
  }

  const replace = [];
  if (target.compareExactSignature) {
    const directByKey = new Map(directMethods.map(method => [methodKey(method.signature), method]));
    for (const method of docMethods) {
      if (!directKeys.has(method.key)) {
        continue;
      }
      const expected = directByKey.get(method.key);
      if (!expected || expected.signature === method.signature) {
        continue;
      }
      replace.push({
        method: method.name,
        documentedSignature: method.signature,
        cppSignature: expected.signature,
        comment: expected.comment,
      });
    }
  }

  if (add.length === 0 && remove.length === 0 && replace.length === 0) {
    return null;
  }

  return {
    language: target.language,
    docPath: target.docPath,
    interface: section.interface,
    add,
    remove,
    replace,
  };
}

const headerFiles = collectHeaderFiles(HEADER_ROOT);
const headerMap = new Map(headerFiles.map(filePath => [path.basename(filePath, ".h"), filePath]));
for (const filePath of headerFiles) {
  const rawText = fs.readFileSync(filePath, "utf8");
  const classMatch = rawText.match(/\bclass\s+(?:\w+\s+)?(I[A-Za-z0-9_]+)\s*(?::[^{;]+)?\{/);
  if (classMatch && !headerMap.has(classMatch[1])) {
    headerMap.set(classMatch[1], filePath);
  }
}
const parseCache = new Map();
const allCache = new Map();
const filteredTargets = TARGETS.filter(target => !languageFilter || target.language === languageFilter);

if (filteredTargets.length === 0) {
  console.error(`未找到语言目标: ${languageFilter}`);
  process.exit(1);
}

const results = [];
let missingInterface = false;

for (const target of filteredTargets) {
  const docContent = fs.readFileSync(target.docPath, "utf8");
  const sections = extractSections(docContent);
  const sectionMap = new Map(sections.map(section => [section.interface, section]));
  const interfaces = interfaceFilter ? [interfaceFilter] : sections.map(section => section.interface);

  for (const interfaceName of interfaces) {
    const section = sectionMap.get(interfaceName);
    if (!section) {
      missingInterface = true;
      continue;
    }
    const headerPath = headerMap.get(interfaceName);
    if (!headerPath) {
      continue;
    }

    let parsed = parseCache.get(interfaceName);
    if (!parsed) {
      parsed = parseHeader(headerPath);
      parseCache.set(interfaceName, parsed);
    }
    const directMethods = parsed.methods;
    const allMethods = collectAllMethods(interfaceName, headerMap, parseCache, allCache);
    const docMethods = extractDocMethods(section.content, target.language);
    const item = buildInterfaceResult(target, section, directMethods, allMethods, docMethods);
    if (item) {
      results.push(item);
    }
  }
}

const output = {
  compareRule: {
    missing: "代码中该接口直接声明的方法，文档章节未写到。",
    remove: "文档章节中的方法，当前代码接口及其继承链中都不存在。",
    replace: "仅对 C++ 文档做精确签名核对；同名同参数个数但签名文本不同，会给出替换建议。",
    note: "Python3/Java 文档按方法名+参数个数核对，因其签名风格与 C++ 头文件不同。",
  },
  summary: {
    repoRoot: REPO_ROOT,
    headerRoot: HEADER_ROOT,
    docsChecked: filteredTargets.map(target => ({ language: target.language, docPath: target.docPath })),
    interfaceFilter: interfaceFilter || null,
    totalChanges: results.length,
    totalAdd: results.reduce((sum, item) => sum + item.add.length, 0),
    totalRemove: results.reduce((sum, item) => sum + item.remove.length, 0),
    totalReplace: results.reduce((sum, item) => sum + item.replace.length, 0),
  },
  results,
};

console.log(JSON.stringify(output, null, 2));

if ((strictMode && results.length > 0) || missingInterface) {
  process.exitCode = 1;
}
