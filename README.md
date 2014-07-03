CM-wowza
========
Wowza plugin for CM

Development
-----------
Make sure you can log in as root to the target VM which is running wowza, e.g. run:
```
ssh-copy-id root@my-vm.dev
```

Run the ant task `dev-restart` to build the jar, copy it into the VM and restart wowza:
```
ant dev-restart -Dvm.host=my-vm.dev
```
