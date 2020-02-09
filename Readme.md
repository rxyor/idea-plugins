# Pom Assistant

# 前言
作为一个Java开发者，公司大部分项目还是maven工程，虽然gradle依赖看起来比较简洁、可以写丰富的Task，但是maven有丰富的插件，也比较简单，相比gradle而言，也没有那么多的坑，还是用地比较多的。同时也有一些问题，比如maven的pom.xml是xml格式，一个依赖至少5行标签。当一个项目比较大时，引用的依赖也比较多，看起来也比较乱，个人习惯上把dependency标签的version提取到properties标签里。但是作者每次这样做就觉比较繁琐，于是心生一个想法，自己写idea插件，方便自己的操作。于是在抗争冠状病毒的这个春节，写了这个插件。

# 功能
在作者开发过程中，也借鉴了其他插件的功能，共实现了一下几个功能：

- 提取dependency/plugin的verion到properties标签里
- 按照特定的顺序，对pom.xml进行排序
- 有时候我们会加入注释的方式进行分组，也可以进行分组排序
- 提供了一个简单的界面可以搜索公共仓库的依赖

# 使用
## 1. 提取并替换版本号
鼠标光标移至特定的依赖标签，打开右键。**Pom Assistant**->**Replace Verion**
**![image.png](https://cdn.nlark.com/yuque/0/2020/png/165192/1581254499261-2959286f-4f83-4fb3-b13c-f9616649d08c.png#align=left&display=inline&height=477&name=image.png&originHeight=954&originWidth=1586&size=326932&status=done&style=none&width=793)**
替换后，版本会被替换为占位符。


## 2. 排序
**Pom Assistant**->**Sort**, 该排序是插件内部按特定排序设定的，dependency是按照groupId进行排序的。
## 3. 分组排序
**Pom Assistant**->**Sort Group**, 该排序是解析XML标签的中的注释，进行分组排序
![image.png](https://cdn.nlark.com/yuque/0/2020/png/165192/1581255849382-08b5d0fb-61cd-4dd4-89a8-f73add3d8423.png#align=left&display=inline&height=260&name=image.png&originHeight=520&originWidth=1140&size=222861&status=done&style=none&width=570)
## 4. 搜索依赖
**Pom Assistant**->**Search**, 可以进行依赖搜索，如下图：
![image.png](https://cdn.nlark.com/yuque/0/2020/png/165192/1581256107697-8f5866d7-6616-4eac-83aa-d4ce9a96be3b.png#align=left&display=inline&height=406&name=image.png&originHeight=812&originWidth=1196&size=168468&status=done&style=none&width=598)


## 5. 动态演示
![1.gif](https://cdn.nlark.com/yuque/0/2020/gif/165192/1581256320404-4acb0482-3c5c-4197-bd3a-218553547612.gif#align=left&display=inline&height=2100&name=1.gif&originHeight=2100&originWidth=3360&size=1285558&status=done&style=none&width=3360)

![2.gif](https://cdn.nlark.com/yuque/0/2020/gif/165192/1581256327480-045f8437-aa09-44ce-bcf7-aa2fe00dafe7.gif#align=left&display=inline&height=760&name=2.gif&originHeight=760&originWidth=627&size=1019325&status=done&style=none&width=627)

![720p.2020-02-09 17_53_02.gif](https://cdn.nlark.com/yuque/0/2020/gif/165192/1581256337755-8cea7f4e-cf2f-43f1-9426-8ea4f4603c76.gif#align=left&display=inline&height=800&name=720p.2020-02-09%2017_53_02.gif&originHeight=800&originWidth=1280&size=475939&status=done&style=none&width=1280)

# 附件
插件地址：[https://plugins.jetbrains.com/plugin/13771-pom-assistant](https://plugins.jetbrains.com/plugin/13771-pom-assistant)，如果你觉得可以，请给5星好评。
