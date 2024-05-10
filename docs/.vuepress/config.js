module.exports = {
  title: '二次开发', // 网站的标题
  description: 'Just playing around', // 网站的描述，它将会以 <meta> 标签渲染到当前页面的 HTML 中
  charset: 'utf-8',
  head: [
    ['link', { rel: 'icon', href: '/logo.png' }] // 需要被注入到当前页面的 HTML <head> 中的标签
  ],
  theme: "reco",
  themeConfig: {
    logo: '/jida_logo.png', //导航栏 Logo
    logoDark: '/jida_logo2.png', //导航栏 Logo
    toolbagAddress: 'https://www.jidatraffic.com/#/', //官网二次开发包下载位置
    nav: [
      // 可指定链接跳转模式：默认target: '_blank'新窗口打开，_self当前窗口打开
      // 支持嵌套,形成下拉式的导航菜单
      {
        text: '语言',
        key: 'language',
        ariaLabel: 'language Menu',
        items: [
          { text: 'Python3', link: '/Python3/' },
          { text: 'C++', link: '/C++/' }
        ]
      },
      {
        text: '版本',
        key: 'version',
        ariaLabel: 'Language Menu',
        items: [
          { text: 'V3.x', link: '/V3.x' },
        ]
      },
      { text: 'TESS NG 官网', link: 'https://www.jidatraffic.com/', target: '_blank' },
      { text: 'GitHub地址', link: 'https://github.com/jida-traffic/TESSNG_SecondaryDev_Doc', target: '_blank' }
    ],
    sidebar: {
      '/document/V3.x/C++': [
        {
          title: '简介',
          path: '/document/V3.x/C++/'
        },
        {
          title: '更新日志',
          path: '/document/V3.x/C++/1-changelog.md'
        },
        {
          title: '软件安装',
          path: '/document/V3.x/C++/install.md'
        },
        {
          title: '快速入门',
          path: '/document/V3.x/C++/quickstart.md'
        },
        {
          title: '接口详解',
          path: '/document/V3.x/C++/details.md'
        },
        {
          title: '典型接口案例',
          path: '/document/V3.x/C++/turorials.md'
        },
        {
          title: '行业案例',
          path: '/document/V3.x/C++/demo.md'
        },
        {
          title: '注意事项',
          path: '/document/V3.x/C++/2-information.md'
        },
        {
          title: '问答列表',
          path: '/document/V3.x/C++/3-QA.md'
        }
      ],
      '/document/V3.x/Python3': [
        {
          title: '简介',
          path: '/document/V3.x/Python3/'
        },
        {
          title: '更新日志',
          path: '/document/V3.x/Python3/1-changelog.md'
        },
        {
          title: '软件安装',
          path: '/document/V3.x/Python3/install.md'
        },
        {
          title: '快速入门',
          path: '/document/V3.x/Python3/quickstart.md'
        },
        {
          title: '接口详解',
          path: '/document/V3.x/Python3/details.md'
        },
        {
          title: '典型接口案例',
          path: '/document/V3.x/Python3/turorials.md'
        },
        {
          title: '行业案例',
          path: '/document/V3.x/Python3/demo.md'
        },
        {
          title: '注意事项',
          path: '/document/V3.x/Python3/2-information.md'
        },
        {
          title: '问答列表',
          path: '/document/V3.x/Python3/3-QA.md'
        }
      ],
      '/': [
        {
          title: '简介',
          path: '/'
        },
        {
          title: '更新日志',
          path: '/document/V3.x/Python3/1-changelog.md'
        },
        {
          title: '软件安装',
          path: '/document/V3.x/Python3/install.md'
        },
        {
          title: '快速入门',
          path: '/document/V3.x/Python3/quickstart.md'
        },
        {
          title: '接口详解',
          path: '/document/V3.x/Python3/details.md'
        },
        {
          title: '典型接口案例',
          path: '/document/V3.x/Python3/turorials.md'
        },
        {
          title: '行业案例',
          path: '/document/V3.x/Python3/demo.md'
        },
        {
          title: '注意事项',
          path: '/document/V3.x/Python3/2-information.md'
        },
        {
          title: '问答列表',
          path: '/document/V3.x/Python3/3-QA.md'
        }
      ]
    },
    subSidebar: 'auto'
  },
  plugins: [
    ['vuepress-plugin-code-copy', true],
    [
      "@vuepress/active-header-links",
      {
        sidebarLinkSelector: ".sidebar-link",
        headerAnchorSelector: ".header-anchor",
      },
    ],
    ['flexsearch-pro', {
      /*
        自定义搜索参数
      */
      searchPaths: ['path1','path2'],    // 搜索路径数组，为空表示搜索全部路径
      searchHotkeys: ['s'],    // 激活搜索控件的热键, 默认是 "s" ，也可以添加更多热键
      searchResultLength: 60,    // 搜索结果展示的字符长度, 默认是60个字节
    }],
  ]
}