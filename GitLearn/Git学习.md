# Git学习

## 基础操作

###  初始化git
``` bash
# 初始化仓库
git init 
```
###  查看状态

```bash
# 获得仓库状态
git status
```

###  提交修改

```bash
# 将文件提交到暂存区
git add <file> 

# 将暂存区的文件提交到仓库, 并附加说明
git commit m <message> 
```
###  回退版本

```bash
# 将仓库版本回退至某一版本
# hard 重置暂存区, 重置工作区
# mixed 重置暂存区, 保留工作区
# soft 保留工作区, 保留暂存区
# HEAD表示当前版本, HEAD^表示前一个版本, HEAD^^表示前两个版本, HEAD~100表示前100个版本
git reset hard <version> 


#查看仓库的提交日志, 可以看到版本号
git log

#查看仓库的所有变动历史, 可以看到版本号
git reflog
```

###  撤销修改

```bash
# 撤销对文件的修改, 回退至最近一次add/commit后的状态
git checkout  <file>

# 将文件从暂存区取出, 不会撤销对其内容的修改
git reset HEAD <file>
```

###  删除文件

```bash
# 删除文件, 这一操作会被记录到暂存区, 若想保留操作则需再次commit
git rm <file>
```

## 远程仓库

###  添加远程仓库

```bash
# 将本地仓库与某一远程仓库origin添加关联, 
git remote add orgin <url>

# 将本地仓库的分支master推送至远程仓库分支origin
git push origin master

# 将本地仓库与远程仓库解绑
git remote rm origin
```

###  仓库克隆

```bash
# 将一个远程仓库克隆到本地
git clone <url>
```

## 分支管理

```bash
#创建新分支
git branch <name>

#列出所有分支
git branch

#切换分支
git checkout <name>
git switch <name>

#将某分支合并到当前分支
git merge <name>

#删除分支
git branch -d <name>
```