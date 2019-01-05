# basics

#### Show last N commits:

``` bash
git log -n 3
```

#### Push new local branch to upstream:

``` bash
git push -u origin JPrime2018
```

#### Remove untracked files from the working tree

```
git clean -f -d [-n]
```
`-f` is for *files*, `-d` for *directories* and `-n` (or `--dry-run`) will show what would be deleted without actually deleting anything.

#### Revert all / specific local changes which have not been added to the index yet:

``` bash
git checkout .
git checkout <path>
```

#### Add files to the index:

|                              | New | Modified | Deleted |
|------------------------------|:---:|:--------:|:-------:|
| `git add .`                  | Yes |    Yes   |   Yes   |
| `git add -A`                 | Yes |    Yes   |   Yes   |
| `git add -u`                 |  No |    Yes   |   Yes   |
| `git add --ignore-removal .` | Yes |    Yes   |    No   |

#### Unstage all / one specific file (i.e. remove from index):

``` bash
git reset
git reset <file>
```

- Show diff in staged files:

``` bash
git diff --staged
```

#### Show diff between two branches:

``` bash
git diff master..JEEConf2018
```

#### Show the changes in `JEEConf2018` but not in `master`

This contains changes from `JEEConf2018` which have already been cherrypicked to `master`!. Reversing the commit range (i.e. `JEEConf2018..master` will show the changes from master which are not yet in `JEEConf2018`):

``` bash
git log --oneline master..JEEConf2018
```

#### Show the changes in `JEEConf2018` OR `master` but not in both

This contains changes from both `master` and `JEEConf2018` which have already been cherrypicked to the other branch!). 

``` bash
git log --oneline master...JEEConf2018
```

The last command is especially usefull together with the `--left-right` option. In that case, each change will be prefixed with `<` or `>` indicating in which of the two branches (i.e. left or right) the corresponding change is in:

```bash
git log --oneline --left-right master...JPrime2018
< 4d7d041 Improved layout of JRuby slides
< 612192d Slightly improved CDS/AOT introduction graphics
< dbd1624 Improved 'Creating a Class List for Custom Loaders' slide
> 081a062 Improved layout of JRuby slides
> ba5d2b0 Slightly improved CDS/AOT introduction graphics
> a96d4b4 Added 'Thanks for your attention'
> 5b93cdc Improved 'Creating a Class List for Custom Loaders' slide
> c4a555f Udated reference links to JPrime2018 repository
```

You can additionally use `--cherry-pick` to exclude changes which have been already cherry-picked from one branch into the other:

```bash
git log --oneline --left-right --cherry-pick master...JPrime2018
> a96d4b4 Added 'Thanks for your attention'
> c4a555f Udated reference links to JPrime2018 repository
```

Finally, with `--cherry-mark` cherry picked changes are displayed with a `=` if they were cherry picked unchanged or with a `+` if they have been changed while cherry picked:

```bash
git log --oneline --left-right --cherry-mark master...JPrime2018
= 4d7d041 Improved layout of JRuby slides
= 612192d Slightly improved CDS/AOT introduction graphics
= dbd1624 Improved 'Creating a Class List for Custom Loaders' slide
= 081a062 Improved layout of JRuby slides
= ba5d2b0 Slightly improved CDS/AOT introduction graphics
> a96d4b4 Added 'Thanks for your attention'
= 5b93cdc Improved 'Creating a Class List for Custom Loaders' slide
> c4a555f Udated reference links to JPrime2018 repository
```
This means for example that `4d7d041` from the left side (i.e. `master`) has been cherry picked from `081a062` from the right side (i.e. `JPrime2018`) without changing.

# stash

- Save local chanegs and index (i.e. staged changes):

``` bash
git stash
git stash list
git checkout <branch|change>
git stash pop
```

# submodules

``` bash
git submodule add -b JEEConf2018 https://github.com/simonis/CDS
git submodule update --init --recursive
```

# rebase interactively

To cherry-pick some changes (and possibly edit them) from a branch (i.e. `GeeCon2018`) into another branch (i.e. `master`) do the following:

First change to the source branch (i.e. `GeeCon2018`) and create a new, temporary branch (e.g. `GeeCon2018_feedback`):

``` bash
git checkout GeeCon2018
git checkout -b GeeCon2018_feedback
```

Then call `rebase --interactive --onto`. The first argument is the destination branch (i.e. `master`). The second argument is the parent of the change from wich to start rebasing (i.e. `master`). The third and last argument is the change until which to rebase (i.e. `GeeCon2018_feedback`). This means that all the changes inbetween `master` (exclusively) and `GeeCon2018_feedback` (inclusively) will be now rebased on top of master:

``` bash
git rebase --interactive --onto master master GeeCon2018_feedback
```

In the up-popping editor you can choose for every change wether it should be `pick`ed or `edit`ed. Also changes can be removed, reordered and `squash`ed. If you choosed to `edit` a change, it will be rebased, but before it will be committed, the following message will appear:

```
Stopped at 019bebe571b58b3dc5dd62263f5173d9ae956aba... Added GeeCON template slides and new slides for JDK 11 enhancements
You can amend the commit now, with

	git commit --amend

Once you are satisfied with your changes, run

	git rebase --continue
```

You can now easily ammend (i.e. edit) the current change. For example to simply change the comment, you could use:


``` bash
git commit --amend -m "Added new slides for JDK 11 enhancements"
git rebase --continue
```

Finally, when all the changes have been rebased but your still not satisified with the result, you can easily refine it by simply repeating the rebase:


``` bash
git rebase --interactive --onto master master GeeCon2018_feedback
```

Once your satisfied with the result, simply merge the temporary `GeeCon2018_feedback` branch into `master` and delete it afterwards:

``` bash
git checkout master
git merge --ff GeeCon2018_feedback
git branch -d GeeCon2018_feedback
```

