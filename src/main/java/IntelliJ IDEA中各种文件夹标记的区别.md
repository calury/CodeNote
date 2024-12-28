1. Source roots (or source folders)

通过将文件夹加入这种类别，来告诉IntelliJ IDEA，这个文件夹和它的子文件夹中包含源码，在构建工程时，需要作为一部分被编译进去。

2. Test source roots (or test source folders; shown as rootTest)

这个类型的文件夹也用于存放源码，不过是测试的源码（比如单元测试）。test source 文件夹可以帮助你将测试代码和产品代码分离开。

3. Resource roots

该类文件夹用于存放你的应用中需要用到的资源文件（如：图片、xml或者properties配置文件等）。

在构建过程中，resources文件夹中的内容均会按照原文件的样子被复制到输出文件夹。和sources文件夹一样，你可以定制你的resources文件夹的结构。你也可以指定你的resources文件夹中的文件被拷贝到输出文件的哪个文件夹中。

ps：默认情况下，工程编译后，resources中的文件和文件夹会被放置在和源码编译后相同的文件夹中，所以如果在源码中以相对路径读取resources中的配置文件时，可认为src中的java文件夹和resources为同一个根目录。

4. Test resource roots

（只有在java模式中可以使用）用于存放测试源码中关联的资源文件。除此之外，和resource文件夹没有区别。




另外补充一下IntelliJ IDEA中的各种文件夹图标：

![img.png](img.png)
