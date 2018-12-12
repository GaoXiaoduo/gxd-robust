# gxd-robust
# 美团Robust热修复接入说明文档

## 环境配置

1）工程build gradle

    classpath 'com.android.tools.build:gradle:3.1.2'
    classpath 'com.meituan.robust:gradle-plugin:0.4.82'
    classpath 'com.meituan.robust:auto-patch-plugin:0.4.82'

**注意：**
robust 0.4.82版本不支持android studio 3.2.1， gradle 4.6，运行此插件请把android studio降为3.1.2，gradle降为4.4

2）app下的build gradle

    apply plugin: 'com.android.application'
    //制作补丁前关闭，制作补丁时将auto-patch-plugin打开，auto-patch-plugin紧跟着com.android.application
    apply plugin: 'auto-patch-plugin'
    //制作补丁前打开，制作补丁时将将robust关闭
    apply plugin: 'robust'
    //加入如下依赖
    implementation 'com.meituan.robust:robust:0.4.82'
    
    配置debug，release的signingConfigs
    开启混淆 minifyEnabled true

## 文件配置

1）在app文件夹下添加robust.xml文件，路径为：app/robust.xml,

文件内容参考github官方文档,复制粘贴进来,做如下修改

    <packname name="hotfixPackage">
        <!--改为自己工程的包名-->
        <name>com.gxd.robust</name>
    </packname>
    <exceptPackname name="exceptPackage">
         <!--可以不添加任何标签-->
    </exceptPackname>
    <patchPackname name="patchPackname">
            <!--改为自己工程的包名-->
            <name>com.gxd.robust.patch</name>
    </patchPackname>

2）在app文件夹下创建robust文件夹，路径为：app/robust,文件夹下内容参考步骤3

3）运行修改前的代码

    //关闭此插件
    //apply plugin: 'auto-patch-plugin'
    //开启此插件
    apply plugin: 'robust'

运行命令：./gradlew clean  assembleRelease --stacktrace --no-daemon

运行成功后，app/build/outputs/文件夹下生成mapping和robust文件夹

将代码运行到手机中

将app/build/outputs/mapping/release/mapping.txt文件复制粘贴到app/robust文件夹下

将app/build/outputs/robust/methodsMap.robust文件复制粘贴到app/robust文件夹下

    目录结构如下：
    |app
    |---|robust
    |--------|mapping.txt
    |--------|methodsMap.robust

修改代码

1）在改动的方法上面添加@Modify注解,对于Lambda表达式请在修改的方法里面调用RobustModify.modify()方法

    @Modify
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
         }
         //或者是被修改的方法里面调用RobustModify.modify()方法
         protected void onCreate(Bundle savedInstanceState) {
            RobustModify.modify()
            super.onCreate(savedInstanceState);
         }

2）新增的方法和字段使用@Add注解

       //增加方法
        @Add
        public String getString() {
            return "Robust";
        }
        //增加类
        @Add
        public class NewAddCLass {
            public static String get() {
               return "robust";
             }
        }

3）加载补丁代码

    new PatchExecutor(getApplicationContext(), new PatchManipulateImp(),
            new RobustCallBackImp()).start();

4）添加手机卡读写权限，获取运行时权限代码

5）获取补丁文件

    public class PatchManipulateImp extends PatchManipulate
    {
        @Override
        protected List<Patch> fetchPatchList (Context context)
        {
          //省略部分代码
          //设置项各个App可以独立定制，需要确保的是setPatchesInfoImplClassFullName设置的包名是和xml配置
          //项patchPackname保持一致，而且类名必须是：PatchesInfoImpl,无需改动
          //请注意这里的设置
          patch.setPatchesInfoImplClassFullName("com.gxd.robust.patch.PatchesInfoImpl");
          //省略部分代码
          return patches;
        }
         @Override
        protected boolean verifyPatch (Context context, Patch patch)
        {
            //省略部分代码
            return true;
        }
        @Override
        protected boolean ensurePatchExist (Patch patch)
        {
            //省略部分代码
            return true;
        }
        public void copy (String srcPath, String dstPath) throws IOException
        {
          //省略部分代码
        }
    }

6）获取补丁回调

    public class RobustCallBackImp implements RobustCallBack
    {
        private static final String TAG = RobustCallBackImp.class.getSimpleName();
    
        @Override
        public void onPatchListFetched (boolean result, boolean isNet, List<Patch> patches)
        {
           //省略部分代码
        }
        @Override
        public void onPatchFetched (boolean result, boolean isNet, Patch patch)
        {
           //省略部分代码
        }
        @Override
        public void onPatchApplied (boolean result, Patch patch)
        {
           //省略部分代码
        }
        @Override
        public void logNotify (String log, String where)
        {
           //省略部分代码
        }
       @Override
        public void exceptionNotify (Throwable throwable, String where)
        {
           //省略部分代码
        }
    }

7）为修改后的代码打补丁包

    //开启此插件
    apply plugin: 'auto-patch-plugin'
    //关闭此插件
    //apply plugin: 'robust'

运行命令：./gradlew clean  assembleRelease --stacktrace --no-daemon

补丁制作成功后会停止构建apk，出现类似于如下的提示,表示补丁生成成功 

    提示：Caused by: java.lang.RuntimeException: auto patch end successfully
    最后 build failed 是正常的

补丁生成成功后会生成patch.jar文件，文件路径为：app/build/outputs/robust/patch.jar

8）将补丁文件copy到手机目录/sdcard/robust下

补丁的路径/sdcard/robust是PatchManipulateImp中指定的

运行命令：

    adb push /Users/GaoXiaoduo/Workspace/Test_Workspace/robustgxd/app/build/outputs/robust/patch.jar /sdcard/robust/patch.jar

9）点击加载按钮后，点击页面跳转，查看更改后的文字已经更改，具体代码查看github的demo

**备注：**

参考接入文档：https://blog.csdn.net/snailbaby_soko/article/details/69524380

github 官方文档：https://github.com/Meituan-Dianping/Robust

github demo下载地址：https://github.com/GaoXiaoduo/gxd-robust.git



