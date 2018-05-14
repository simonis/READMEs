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

Then call `rebase --interactive --onto`. The first argument is the destination branch (i.e. `master`). The second argument is the parent of the change from wich to start rebasing (i.e. `master`). The third and last argument is the change until which to rebias (i.e. `GeeCon2018_feedback`). This means that all the changes inbetween `master` (exclusively) and `GeeCon2018_feedback` (inclusively) will be now rebased on top of master:

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

