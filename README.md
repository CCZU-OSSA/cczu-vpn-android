<div align=center>
  <h1 align="center">吊大VPN</h1>
</div>

<div align=center>

一款让你随时随地~~抛弃垃圾无线~~连接校园网的应用

<img src="https://img.shields.io/badge/Kotlin-1.9.0-green" alt="Kotlin">
<img src="https://img.shields.io/github/languages/code-size/CCZU-OSSA/cczu-vpn-android?color=green" alt="size">
</div>

## 为什么有这个

~~还不是因为CCZU-CMCC寄了~~

是为了方便大家在无法连接到校园无线的时候访问校园网资源

## 平台支持

| Windows | Android | Linux | MacOS | IOS |
|---------|---------|-------|-------|-----|
| ❌       | ✅       | ❌     | ❌     | ❌   |

除了Android平台外，其余平台vpn服务由 [常大助手](https://github.com/CCZU-OSSA/cczu-helper) 提供

## 参与本项目

### 反馈意见

如果不知道如何在Github提issue，可以搜一下`如何提issue`

https://github.com/CCZU-OSSA/cczu-vpn-android/issues

### 如何编译

编译之前先确保你的设备上拥有 AndroidSDK, Rust, OpenSSL 与 JDK，需要`clone`此项目你还需要一个`git`

```sh
rustup target add x86_64-linux-android armv7-linux-androideabi aarch64-linux-android i686-linux-android
```

然后运行以下代码

```sh

git clone https://github.com/CCZU-OSSA/cczu-vpn-android.git
cd cczu-vpn-android
./gradlew assembleDebug
```

### Windows 编译

由于项目有使用Openssl,因此在Windows下编译较为麻烦

你需要安装Python < 3.13, AndroidSDK, Rust(stable-x86_64-pc-windows-gnu) 与 JDK

首先安装msys2,并在msys2中执行以下指令:

```sh
pacman -S perl make
```

然后将 `msys2安装目录/bin` 添加到 `Path` 环境变量中, 然后重启电脑后编译此项目