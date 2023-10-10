# continueKey
FGO日服引继码转存档-腾讯云函数
## 部署方法
1.编译jar包后部署到腾讯云函数，函数入口为`cn.mcfun.utils.EncryptFile::getFile`，地域尽量选择日本地区，大陆地区可能会出现网络问题导致请求错误。

![image](https://user-images.githubusercontent.com/75831884/168506391-43212a58-d613-48f6-bd94-0f7eb81aa935.png)

2.在函数配置中启用文件系统，远程目录设置为`/`，本地目录设置为`/mnt`，内存尽量设置256M以上，函数超时时间尽量10秒以上。

![image](https://user-images.githubusercontent.com/75831884/168716779-10fd54c5-aa33-4221-8ab0-7f5d03f5a09e.png)

3.创建触发器，选择API网关触发，会生成一个链接，如`https://service-pv3w1woy-1303287969.jp.apigw.tencentcs.com/release/continueKey`

![image](https://user-images.githubusercontent.com/75831884/168507346-9eb831ed-de00-4805-a159-f632debd2e52.png)

## 使用方法(均为GET请求)
1.引继码转存档

示例：`https://service-pv3w1woy-1303287969.jp.apigw.tencentcs.com/release/continueKey?key=8rHQnTYmfB&pwd=1234`

`key`引继码，`pwd`密码

```json
{
	"continueKey": "JaGbzYXqC2",  #新引继码
	"continuePass": "3794", #新密码
	"saveData": "ZSv/WkOGi...eOqhov6uXQ==",  #存档
	"userId": "61379147", #用户id
	"authKey": "gV+NTSiL7TQdFeaS:at32AwAAAAA=",
	"secretKey": "d0KnrAw/iltk+mex:at32AwAAAAA="
}
```

2.存档转引继码

示例：`https://service-pv3w1woy-1303287969.jp.apigw.tencentcs.com/release/continueKey?saveData=ZSv%2FWkOGi...eOqhov6uXQ%3D%3D`

`saveData`存档（在浏览器中输入，要先进行url编码）

```json
{
	"continueKey": "FC2uymnw6T",  #新引继码
	"continuePass": "9131", #新密码
	"userId": "61379147"  #用户id
}
```

3.userId，authKey，secretKey转引继码

示例：`https://service-pv3w1woy-1303287969.jp.apigw.tencentcs.com/release/continueKey?userId=61379147&authKey=gV%2BNTSiL7TQdFeaS%3Aat32AwAAAAA%3D&secretKey=d0KnrAw%2Filtk%2Bmex%3Aat32AwAAAAA%3D`

`userId`用户id，`authKey`authKey，`secretKey`secretKey（在浏览器中输入，要先进行url编码）

```json
{
	"continueKey": "FC2uymnw6T",  #新引继码
	"continuePass": "9131", #新密码
	"userId": "61379147"  #用户id
}
```
