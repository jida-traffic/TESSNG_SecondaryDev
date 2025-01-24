from MyPlugin import MyPlugin


if __name__ == '__main__':
    tess_file_path: str = "三清高速.tess"
    my_tess = MyPlugin(tess_file_path)
    my_tess.start()
