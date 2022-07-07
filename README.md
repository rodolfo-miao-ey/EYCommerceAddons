# EYCommerceAddons
SAP Commerce EY Addons 

## Config Guide

### Version - Hybris v2015
https://help.sap.com/viewer/a74589c3a81a4a95bf51d87258c0ab15/2105/en-US/8bf5a611866910149242e1a3a41eb9af.html

### Before start
1.  Download Hybris version 2015 [here](https://sites.ey.com/:u:/r/sites/EYSAPCOMMERCE/Shared%20Documents/General/CXCOMM210500P_9-70005661.ZIP?csf=1&web=1&e=fcS2Kh).
2.  Clone the repository on folder /opt  (will create directory "EYCommerceAddons").
3.  Change current branch to develop with: ```git checkout develop```
4.  Go to path EYCommerceAddons, extract the contente from downloaded zip file in step 1 on this directory.

### Initializing the environment
1.  Go to path EYCommerceAddons/installer and run: ```./install.sh -r ey -A initAdminPassword=nimda```
2.  After a successful build, go to ```EYCommerceAddons/hybris/bin/platform```.
3.  Run ```. ./setantenv.sh```
4.  Run ```ant initialize```

#### Starting the server
1. At directory ```EYCommerceAddons/hybris/bin/platform```, run ```./hybrisserver.sh debug```

### Configuring hosts file 

Run command sudo nano /etc/hosts to open hosts files on vim editor and add above lines:
```bash

127.0.0.1 electronics.local
127.0.0.1 powertools.local
