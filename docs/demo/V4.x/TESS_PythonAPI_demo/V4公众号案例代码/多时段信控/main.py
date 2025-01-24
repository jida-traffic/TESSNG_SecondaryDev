from MyPlugin import MyPlugin


if __name__ == '__main__':
    tess_file_path: str = ""
    my_tess = MyPlugin(tess_file_path)
    my_tess.start()
