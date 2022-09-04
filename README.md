# 安卓蓝牙Demo

## 开篇提示

本App基于**蓝牙2.0**开发,还请注意,本教程演示手机型号 **XIAOMI 8**

## ❗ 权限设置

需要开启**蓝牙权限**和**定位权限**

开启定位权限是由于 **Android 10.+** 以上的要求

<div align=left>
<img src="https://img-blog.csdnimg.cn/20201126175854106.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80MzY5OTcxNg==,size_16,color_FFFFFF,t_70#pic_center" width = 50% height = 50%/>
</div>


## 操作提示

应用Demo主要提供以下几种功能：

<div align=left>
<img src="IMG/menu.jpg" width = 30% height = 30%/>
</div>


### 打开可见性

**此操作是必要的**，点击菜单栏的 **打开可见** 使得自己的设备能够被查看到

### 寻找设备

点击 **寻找设备** 会搜索附近蓝牙设备(❗安卓10.+以后要打开定位,否则无法搜索到设备)

<div align=left>
<img src="IMG/searchDevice.jpg" width = 30% height = 30%/>
</div>

点击列表中的设备即可配对，我们以 **HC-06** 为例

<div align=left>
<img src="IMG/bond.jpg" width = 30% height = 30%/>
</div>

绑定成功会有弹窗提示

<div align=left>
<img src="IMG/bondSuccessful.jpg" width = 30% height = 30%/>
</div>

### 已绑定

点击 **已绑定** 会显示已绑定设备，我们点击已绑定设备进行连接，连接成功会有弹窗提示，我们以 **HC-06** 为例

<div align=left>
<img src="IMG/connectSuccessful.jpg" width = 30% height = 30%/>
</div> 

### 发送Hello Hi

连接成功后，打开串口助手，点击App菜单栏内的 **发送Hello**，**发送Hi**，可以在串口助手看到消息

<div align=left>
<img src="IMG/serialPortAssistant.png" width = 50% height = 50%/>
</div> 

## 调试设备

**蓝牙模块** 和 **TTL转USB**

<div align=left>
<img src="IMG/debug.jpg" width = 50% height = 50%/>
</div> 
