# flair-tbox

慧翰T-BOX

## 前提条件
- [Git 1.9+](http://git-scm.com/downloads)
- [nvm 1.1+](https://github.com/creationix/nvm)

### 前提条件 - 环境配置
执行以下命令,确认版本符合前提条件中指定的要求
```SHELL
git --version
nvm version
```

### 前提条件 - node
从 http://coreybutler.github.io/nodedistro/ 查询可供使用的node版本(建议使用node 7.2+)
执行以下命令配置node环境
```SHELL
nvm install 7.2.1
nvm use 7.2.1
nvm on
```

### 前提条件 - 依赖组件
执行以下命令安装依赖组件
```SHELL
npm install
```
提示: 在中国内地可以使用--registry参数来指定taobao的镜像来执行npm组件的安装
```SHELL
npm install --registry=http://registry.npm.taobao.org
```

## Build Setup

``` bash
# install dependencies
npm install

# serve with hot reload at localhost:8080
npm run dev

# build for production with minification
npm run build