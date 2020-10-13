# basics

#### Show last N commits:

```
git log -n 3
```

#### Graphical log

```
git log --all --graph --oneline
```

#### Show changes and files at revision

```
git show <rev>
```

`<rev>` can be a tag, branch or change SHA.

```
git show <rev>:<file>
```

Shows `<file>` at the revision `<rev>`.

#### Push new local branch to upstream (`-u` = `--set-upstream`):

```
git push -u origin JPrime2018
```

#### Remove untracked files from the working tree

```
git clean -f -d [-n]
```
`-f` is for *files*, `-d` for *directories* and `-n` (or `--dry-run`) will show what would be deleted without actually deleting anything.

#### Revert all / specific local changes which have not been added to the index yet:

```
git checkout .
git checkout <path>
```
#### Revert the last commit(s)

```
git reset HEAD^
git reset --hard HEAD^
git reset HEAD~2
```

The first command reverts the last commit but keeps the local changes. The second command reverts the last commit AND removes the local changes. The third command reverts the last two commits. Following this pattern, even more commits can be remmoved.


#### Add files to the index:

|                              | New | Modified | Deleted |
|------------------------------|:---:|:--------:|:-------:|
| `git add .`                  | Yes |    Yes   |   Yes   |
| `git add -A`                 | Yes |    Yes   |   Yes   |
| `git add -u`                 |  No |    Yes   |   Yes   |
| `git add --ignore-removal .` | Yes |    Yes   |    No   |

#### Unstage all / one specific file (i.e. remove from index):

```
git reset
git reset <file>
```

- Show diff in staged files:

```
git diff --staged
```

#### Export committed changes to local patch files

```
git format-patch -2
```

`-2` means the last two revisions. This will crear the files `0001-<Change_1_Subject>.patch` and `0002-<Change_2_Subject>.patch` in the local directory.

Use `--stdout` to redirect the diff to stdout.

Use `-1 <hash>` to export the change with hash ID `<hash>`

#### Import chengers from a patch file

```
git apply <file>
git apply --check <file>
```
The first version imports the changes from `<file>` without creating a new change (much like `patch < <file>`). Ther second version (with `--check`) only checks if the patch will apply cleanly (like `patch --dry-run < <file>`).

```
git am <file>
```
This creates a new, committed change from `<file>`. It can be used to directly import changes exported with `git format-patch` before.

#### Show diff between two branches:

```
git diff master..JEEConf2018
```

#### Show the changes in `JEEConf2018` but not in `master`

This contains changes from `JEEConf2018` which have already been cherrypicked to `master`!. Reversing the commit range (i.e. `JEEConf2018..master` will show the changes from master which are not yet in `JEEConf2018`):

```
git log --oneline master..JEEConf2018
```

`JEEConf2018` can be omitted if you'Re already on that branch. Notice that this command shows merge changes from `master` into `JEEConf2018`.

Another possibility to get simialr information (but without the merge changes) is the following command (again, `JEEConf2018` can be omiited, if you're already on that branch):

```
git cherry -v master JEEConf2018
```

#### Show the changes in `JEEConf2018` OR `master` but not in both

This contains changes from both `master` and `JEEConf2018` which have already been cherrypicked to the other branch!). 

```
git log --oneline master...JEEConf2018
```

The last command is especially usefull together with the `--left-right` option. In that case, each change will be prefixed with `<` or `>` indicating in which of the two branches (i.e. left or right) the corresponding change is in:

```console
$ git log --oneline --left-right master...JPrime2018
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

```console
$ git log --oneline --left-right --cherry-pick master...JPrime2018
> a96d4b4 Added 'Thanks for your attention'
> c4a555f Udated reference links to JPrime2018 repository
```

Finally, with `--cherry-mark` cherry picked changes are displayed with a `=` if they were cherry picked unchanged or with a `+` if they have been changed while cherry picked:

```console
$ git log --oneline --left-right --cherry-mark master...JPrime2018
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

#### Is an update available ? (similar to `hg incoming`)

```
git ls-remote --heads <repository>
```

Lists all the remote heads with their change id. `<repository>` defaults to `origin` but can be set to any arbitrary remote or reopsitory URL. Example:

```console
$ git ls-remote --heads
From http://github.com/SAP/SapMachine
5ad2b5f9029c9a1ab926ee49990b24074370d298	refs/heads/sapmachine
76cc6fb9b82510d61fdea5024856f2bd835b9eaf	refs/heads/sapmachine10
5ae9140265b865952412802119b060c969fdeb1f	refs/heads/sapmachine11
c8db5f344e24e15d40f372cfcf0125f544f22ad4	refs/heads/sapmachine12
$ git show sapmachine11
commit 5ae9140265b865952412802119b060c969fdeb1f
...
```

Or you can use `show-ref` to see the local heads and/or tags:

```console
$ git show-ref --heads
321d1442d5fbf751bf6af329950dd901d182a682 refs/heads/jdk-updates/jdk11u
f09d78b55d2adea21f8168a3385a3455f95b6612 refs/heads/jdk/jdk
d58ba44b16bf63aac6f60d03a8f0051d64a0b1fe refs/heads/jdk/jdk11
0bea89f00a31145580159854b770662c1774723f refs/heads/sapmachine
5ae9140265b865952412802119b060c969fdeb1f refs/heads/sapmachine11
```

The remote head of the `sapmachine11` branch is the same like the local one (i.e. `git show sapmachine11
commit 5ae9140265b865952412802119b060c969fdeb1f`) so `git fetch/pull` won't fetchany new changes.

#### Pull with "*take-theirs*" after remote has changed history (i.e. *[forced update](https://stackoverflow.com/questions/9813816/git-pull-after-forced-update)*)

If you're on the `master` branch and upstream (i.e. `origin/master`) was forceably updated, do: 
```
$ git fetch
$ git reset origin/master --hard
```

#### Delete/Rename Branch

Delete a local branch
```
$ git branch -d <branchname>
```

Rename a local branch
```
$ git branch -m <old-name> <new-name>
```

Unset upstream of a local branch
```
$ git branch --unset-upstream <branchname>
```

Set a new upstream for a local branch
```
$ git checkout <branchname>
$ git --set-upstream-to=origin/<branchname>
```
If you did some local changes which changed the history of `<branchname>` you'll have to do a `push --force` to push the new branch upstream.

Delte remote branch
```
$ git push origin --delete <branchname>
```

#### Remotes

```console
$ git remote show
origin
wdf
$ git remote show origin
* remote origin
  Fetch URL: http://github.com/SAP/SapMachine
  Push  URL: http://github.com/SAP/SapMachine
  HEAD branch: sapmachine
  Remote branches:
    sapmachine10                                       tracked
    sapmachine11                                       tracked
    sapmachine12                                       tracked
  Local branches configured for 'git pull':
    jdk-updates/jdk11u merges with remote jdk-updates/jdk11u
    jdk/jdk            merges with remote jdk/jdk
    jdk/jdk11          merges with remote jdk/jdk11
    sapmachine         merges with remote sapmachine
    sapmachine11       merges with remote sapmachine11
  Local refs configured for 'git push':
    jdk-updates/jdk11u pushes to jdk-updates/jdk11u (local out of date)
    jdk/jdk            pushes to jdk/jdk            (local out of date)
    jdk/jdk11          pushes to jdk/jdk11          (up to date)
    sapmachine         pushes to sapmachine         (local out of date)
    sapmachine11       pushes to sapmachine11       (up to date)
```
#### Ad a new remote

```console
git remote add corretto8-upstream https://github.com/corretto/corretto-8.git
```
#### [Syncing a fork](https://help.github.com/en/github/collaborating-with-issues-and-pull-requests/syncing-a-fork)

```console
$ git fetch https://help.github.com/en/github/using-git/adding-a-remote
$ git checkout develop
$ git merge --ff-only corretto8-upstream/develop
$ git push
```

#### Defining a default remote

If you want to prevent to accidentaly push to a wrong remote, a default remote for all pushes can be defined:

```
git config remote.pushDefault <remote>
```

More fine grained configurations are possible by setting [`branch.<name>.remote`](https://git-scm.com/docs/git-config#Documentation/git-config.txt-branchltnamegtremote) or [`branch.<name>.pushRemote`](https://git-scm.com/docs/git-config#Documentation/git-config.txt-branchltnamegtpushRemote).

#### Finding a branch point with Git

This doesn't really seems to be possible in general. See https://community.atlassian.com/t5/Bitbucket-questions/Knowing-from-which-branch-the-current-branch-was-created-from/qaq-p/570135, https://stackoverflow.com/questions/3161204/how-to-find-the-nearest-parent-of-a-git-branch and https://stackoverflow.com/questions/1527234/finding-a-branch-point-with-git.

The best we can come up with is something like:
```
diff -u <(git rev-list --first-parent feature) <(git rev-list --first-parent master) | sed -ne 's/^ //p' | head -1
```
This assumes that you have branched `feature` from `master` at some point in time and you may have after that merged `master` into `feature` several time (but not the other way round. You may also have to remove the `--first-parent` before `master` if master contains merge changes.

# stash

- Save local changes and index (i.e. staged changes):

```
git stash
git stash list
git checkout <branch|change>
git stash pop
```

# submodules

```
git submodule add -b JEEConf2018 https://github.com/simonis/CDS
git submodule update --init --recursive
```

# rebase (simple)

To rebase a branch `<my-branch>` on top of `master` (e.g. after you pulled `master` from `origin` or `upstream`) do the following:
```
git checkout <my-branch>
git rebase master
```
This will replay all changes which are on `<my-branch>` and not on `master` onto `master`. You can find the merge point (i.e. the change where `<my-branch>` branched off from `master` with the `merge-base` command:
```
git merge-base <my-branch> master
```

For more complicated rebases you can use:
```
git rebase --onto master <other-branch> <my-branch>
```
This will rebase all changes from `<my-branch>` since it was branched from `<other-branch>` onto `master`. See the [Git-book](https://git-scm.com/book/en/v2/Git-Branching-Rebasing) for more exmaples and graphs.

# rebase interactively (i.e. cherry-picking)

To cherry-pick some changes (and possibly edit them) from a branch (i.e. `GeeCon2018`) into another branch (i.e. `master`) do the following:

First change to the source branch (i.e. `GeeCon2018`) and create a new, temporary branch (e.g. `GeeCon2018_feedback`):

```
git checkout GeeCon2018
git checkout -b GeeCon2018_feedback
```

Then call `rebase --interactive --onto`. The first argument is the destination branch (i.e. `master`). The second argument is the parent of the change from wich to start rebasing (i.e. `master`). The third and last argument is the change until which to rebase (i.e. `GeeCon2018_feedback`). This means that all the changes inbetween `master` (exclusively) and `GeeCon2018_feedback` (inclusively) will be now rebased on top of master:

```
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


```
git commit --amend -m "Added new slides for JDK 11 enhancements"
git rebase --continue
```

or, if you don't want to change the original commit message:

```
git commit --amend --no-edit
git rebase --continue
```

Finally, when all the changes have been rebased but your still not satisified with the result, you can easily refine it by simply repeating the rebase:

```
git rebase --interactive --onto master master GeeCon2018_feedback
```

> If `git rebase` aborts with an error saying somthing like `error: Your local changes to the following files would be overwritten by merge`, this can be easily fixed by running the rebase command with the "`-c core.trustctime=false`" option. Read the [A Simple Tweak for Making 'git rebase' Safe on OS X](https://www.git-tower.com/blog/make-git-rebase-safe-on-osx/) blog if you're interested why this happens and how the workaround fixes the problem.

Once your satisfied with the result, simply merge the temporary `GeeCon2018_feedback` branch into `master` and delete it afterwards:

```
git checkout master
git merge --ff GeeCon2018_feedback
git branch -d GeeCon2018_feedback
```

# Import a Mercurial changeset into Git

In Mercurial, export a change to a file:

```
hg export -o <filename> -r <revision>
```

The header of the created file looks as follows:

```console
$ head <file>
# HG changeset patch
# User simonis
# Date 1553712467 -3600
#      Wed Mar 27 19:47:47 2019 +0100
# Node ID 0223b7b8a1c5f3ae69ae79d03ddcea983718cc87
# Parent  3fedbfdb25b636ea34a778c38017924016bffc82
8220528: [AIX] Fix basic Xinerama and Xrender functionality
Reviewed-by: clanger, stuefe, serb

diff -r 3fedbfdb25b6 -r 0223b7b8a1c5 src/java.desktop/unix/native/libawt_xawt/awt/awt_GraphicsEnv.c
```

This has to be changed to look as follows:

```console
$ head <file>
From: simonis <none@none>
Date: Wed Mar 27 19:47:47 2019 +0100
Subject: 8220528: [AIX] Fix basic Xinerama and Xrender functionality
  Reviewed-by: clanger, stuefe, serb

diff -r 3fedbfdb25b6 -r 0223b7b8a1c5 src/java.desktop/unix/native/libawt_xawt/awt/awt_GraphicsEnv.c
```
Now, this change can be imported into Git with [`git am`](https://git-scm.com/docs/git-am):

```console
$ git am <file>
```
