# JIMU组件化开发流程

github：https://github.com/mqzhangw/JIMU
目前公司的项目用到了组件化开发，这里记录一下开发流程，基本就是官网的文档，我只是整理了一下，加了一些说明，重点记录一下如何填坑。



## 1. 配置

### 配置根目录

在**根目录**的**gradle.properties**文件中，增加属性：

```java
mainmodulename=app
```

其中mainmodulename是项目中的host工程名称(主模块），一般名字为app

在**根目录**的**build.gradle**中增加配置

```java
buildscript {
    dependencies {
        classpath 'com.luojilab.ddcomponent:build-gradle:1.2.0'
    }
}
```

### 配置主模块和每个组件

#### 配置build.gradle文件

在主模块和每个组件都增加如下插件：

**注意，只有主模块和组件模块才需要这样配置，而平时用的library模块不需要做如下改动**

```java
apply plugin: 'com.dd.comgradle'
```

在主模块和每个组件都加入如下依赖，如果项目中存在basiclib等基础lib库，可以统一交给basiclib引入

```java
compile 'com.luojilab.ddcomponent:componentlib:1.3.0'
```

**注意：主模块和组件模块不需要再引用com.android.application或者com.android.library**

在主模块和每个组件都增加以下配置，设置组件单独运行时候的Application（如果不需要单独运行可以不设置），其中**isRegisterCompoAuto必须设置并且必须为true**，否则会编译错误，这个属性是什么意思暂时不懂：

```java
combuild {
    applicationName = 'com.luojilab.reader.runalone.application.ReaderApplication'
    isRegisterCompoAuto = true
}
```

组件注册还支持反射的方式

如果项目里有UI跳转，还需要其他配置，请看下面的步骤 **3. UI跳转**

#### 配置gradle.properties文件

在主模块和每个组件都增加如下配置，isRunAlone是必须设置的，否则编译不通过，**如果需要单独运行，就设置为true，否则设置为false**：

```java
isRunAlone=true
debugComponent=sharecomponent
compileComponent=sharecomponent
```

上面三个属性分别对应是否单独调试、debug模式下依赖的组件，release模式下依赖的组件。

### 配置主模块的gradle.properties文件

有个需要**注意**的地方，主模块必须在gradle.properties文件里配置debugComponent和compileComponent，如下所示：

```java
isRunAlone=true
debugComponent=module1,module2
compileComponent=module1,module2
```

不配置的话，组件是不会打包到主模块的

## 2. 组件数据交互

### 2.1. 所有组件的服务接口都统一定义在componentService(名字随意)这个module中

为了增加可读性，所有组件的服务都统一定义在componentService这个module中，例如reader组件需要向外提供服务，在componentService增加readerbook文件夹，定义ReadBookService

```java
public interface ReadBookService {
    Fragment getReadBookFragment();
}
```

### 2.2. 组件提供具体实现类

```java
public class ReadBookServiceImpl implements ReadBookService {
    @Override
    public Fragment getReadBookFragment() {
        return new ReaderFragment();
    }
}
```

### 2.3. 将实现类注册到Router中

实现类的注册时机在每个组件的ApplicationLike中，ApplicationLike相当于每个组件的Application类，控制组件的生命周期。

在组件加载的时候进行注册，同时在组件卸载的时候进行反注册。

```java
public class ReaderAppLike implements IApplicationLike {
    Router router = Router.getInstance();
    @Override
    public void onCreate() {
        router.addService(ReadBookService.class.getSimpleName(), new ReadBookServiceImpl());
    }

    @Override
    public void onStop() {
        router.removeService(ReadBookService.class.getSimpleName());
    }
}
```

**经过上面3个步骤，就可以在其他组件中获取这个服务，进而获取所需数据了**

### 其他组件调用服务

由于代码隔离，只有componentService这个module中的接口们（建议起名为XXXService）才对组件们可见，所以只能针对这些接口进行编程。

通过Router获取服务的具体实现，使用前需要判空。

```java
Router router = Router.getInstance();
if (router.getService(ReadBookService.class.getSimpleName()) != null) {
    ReadBookService service = (ReadBookService) router.getService(ReadBookService.class.getSimpleName());
    fragment = service.getReadBookFragment();
    t = getSupportFragmentManager().beginTransaction();
    ft.add(R.id.tab_content, fragment).commitAllowingStateLoss();
}
```



## 3. UI跳转

上面的步骤仅仅可以通过获取服务来交换数据，但是我们肯定需要跳转页面，所以除了注册Router外我们还需要注册组件到UIRouter中

#### 3.1 组件添加必要的依赖

在组件的build.gradle中添加依赖

```java
//java需要下面的
annotationProcessor 'com.luojilab.ddcomponent:router-anno-compiler:1.0.0'
//kotlin需要下面的
kapt 'com.luojilab.ddcomponent:router-anno-compiler:1.0.0'
//两种方式选择一种就可以，如果用kotlin只需要下面那句，否则只需要上面那句
```

同时添加

```java
//java
defaultConfig {
    javaCompileOptions {
        annotationProcessorOptions {
             arguments = [host: "share"]
        }
    }
}
//kotlin
kapt {
    arguments {
        arg("host", "share")
    }
}
//两种方式选择一种就可以，如果用kotlin只需要下面那句，否则只需要上面那句，这些句子写在android标签外面或者里面都可以
```

此处的"share"是跳转URI中的host，每个组件需要设置不同的host。



### 3.2 注册组件到UIRouter中

在组件的声明周期类ApplicationLike中，添加注册和反注册代码

```java
public class ShareApplike implements IApplicationLike {
    UIRouter uiRouter = UIRouter.getInstance();
    @Override
    public void onCreate() {
        uiRouter.registerUI("share");
    }
    @Override
    public void onStop() {
        uiRouter.unregisterUI("share");
    }
}
```



### 3.3 目标页面添加注解

```java
@RouteNode(path = "/shareBook", desc = "分享书籍页面")
public class ShareActivity extends AppCompatActivity {
    。。。
}
```

如果需要传入参数，在具体的参数定义上增加Autowired注解：

```java
@Autowired(name = "bookName")//name也可以不设置，不设置的话默认是字段名
String bookName;
@Autowired
Author author;
```

**注意此处的参数需要设置为非private，否则编译会直接报错，如果是kotlin代码还需要加@JvmField，这样kotlin才会编译成public字段而不是private字段加set/get方法**

### 3.4 自动装载

如果想使用自动装载功能，需要在Activity的onCreate中调用方法，调用该方法后，在类中配置的@Autowired才会有数据

```java
AutowiredService.Factory.getInstance().create().autowire(this);
```

建议该方法在基类Activity中调用，如果在Fragment有@Autowired注解的字段，需要手动在Activity中传给Fragment，否则会空指针异常，因为调用openUri跳转的是Activity，参数传给Activity了，如果不手动传给Fragment的话Fragment的arguments并没有值，再去get东西的时候就会报空指针异常。

### 3.5 build项目

项目执行build，会生成apt文件，具体可在build目录下面查看，同时还会在根目录生成UIRouterTable文件夹，里面会列出每个组件向外提供的路由表。

```java
auto generated, do not change !!!! 

HOST : share

分享书籍页面(@RouteNode配置的desc)
/shareBook(@RouteNode配置的path)
author:com.luojilab.componentservice.share.bean.Author(@Autowired配置的参数)
bookName:String(@Autowired配置的参数)
```



### 3.6 跳转页面

在发起跳转页面，有三种方式可以跳转到目的页面

#### 3.6.1 Bundle方式
```java
// UI transfer with Bundle
private void goToShareActivityWithBundle() {
    Author author = new Author();
    author.setName("Margaret Mitchell");
    author.setCounty("USA");
    Bundle bundle = new Bundle();
    bundle.putString("bookName", "Gone with the Wind");
    bundle.putString("author", JsonService.Factory.getInstance()
            .create().toJsonString(author));
    String path = "DDComp://share/shareBook";//或者share/shareBook
    UIRouter.getInstance().openUri(getActivity(), "DDComp://share/shareBook", bundle);
}
```
#### 3.6.2 URI方式

```java
// UI transfer with URI
private void goToShareActivityWithUri() {
    Author author = new Author();
    author.setName("Barack Obama");
    author.setCounty("New York");
    final String URI_LEGAL = "DDComp://share/shareMagazine?bookName=NYTIME&author=";
    //URI_LEGAL能不能去掉DDComp://我没有测试，有兴趣可以自己测试一下
    legal and illegal data delivering*/
    UIRouter.getInstance().openUri(
    		getActivity(),
            URI_LEGAL + JsonService.Factory.getInstance().create().toJsonString(author), null);
}
```

#### 2.6.3 startActivityForResult

```java
//startActivityForResult
private void goToShareActivityForResult() {
    Author author = new Author();
    author.setName("Margaret Mitchell");
    author.setCounty("USA");
    UIRouter.getInstance().openUri(
    		getActivity(),
        	//下面的代码能不能去掉DDComp://我没有测试，有兴趣可以自己测试一下
            "DDComp://share/shareBook?bookName=Gone with the Wind&author="
             + JsonService.Factory.getInstance().create().toJsonString(author), null, 7777);
}
```

---

#### JIMU框架代码内部主要流程

在执行uiRouter.registerUI("game")的时候会把当前组件自动生成的BaseCompRouter的子类（一个组件一个BaseCompRouter的子类，如刚才注册game就会生成GameUiRouter，代码路径在该模块的build/generated/source/kapt/debug(或者release)/com.luojilab.gen.router），uiRouter.registerUI方法会将刚才生成的GameUiRouter类添加到UIRouter类的uiRouters这个集合里，在跳转页面的时候（UIRouter.getInstance().openUri）会遍历这个uiRouters集合，执行每个BaseCompRouter子类的openUri方法，该方法如果能自己处理就返回true，否则返回false。

自动生成的BaseCompRouter的子类执行openUri方法时会查找当前组件中用@RouteNode配置的页面（Activity），如果当前BaseCompRouter的子类第一次运行会执行initMap，在这个方法里会把这个页面保存到routeMapper，key是这个页面配置的path，value是这个页面。然后在openUri方法里会根据传过来的uri找到在routeMapper中对应的Activity，然后执行startActivityForResult或者startActivity方法。

---

## 开发流程

1. 按照上面的**配置**先配置好JIMU的依赖
2. 在公共库创建Service接口，Activity、Fragment父类等公共类和公共资源
3. 每个组件项目继承公共库的Service接口，提供其他组件需要的实例
4. 在每个组件添加IApplicationLike的子类，不需要手动注册该子类
5. 如果有页面跳转，还需要配置页面的@RouteNode



demo在github的地址：https://github.com/ikakaxi/JIMUDemo

个人简书：https://www.jianshu.com/u/b433b31eadad

个人博客：http://liuhc.me



个人博客和简书同步更新，会先更新简书，顺便吐槽一下简书为什么不能搜索自己的文章。。。