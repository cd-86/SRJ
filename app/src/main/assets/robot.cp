{
 "deviceTypes": [
  {
   "name": "laser",
   "devices": [
    {
     "name": "laser",
     "isDisplay": true,
     "isEnabled": true,
     "deviceParams": [
      {
       "key": "basic",
       "type": "arrayParam",
       "arrayParam": {
        "params": [
         {
          "key": "x",
          "type": "double",
          "doubleValue": 0.00039996065947955906
         },
         {
          "key": "y",
          "type": "double",
          "doubleValue": 0.012182450260087543
         },
         {
          "key": "yaw",
          "type": "double",
          "doubleValue": 0.77584653044052643
         }
        ]
       }
      }
     ]
    }
   ]
  },
  {
   "name": "motor",
   "devices": [
    {
     "name": "left",
     "isDisplay": true,
     "isEnabled": true,
     "deviceParams": [
      {
       "key": "basic",
       "type": "arrayParam",
       "arrayParam": {
        "params": [
         {
          "key": "y",
          "type": "double",
          "doubleValue": -0.00065456142468817147
         }
        ]
       }
      },
      {
       "key": "func",
       "type": "comboParam",
       "comboParam": {
        "childParams": [
         {
          "key": "walk",
          "params": [
           {
            "key": "wheelRadius",
            "type": "double",
            "doubleValue": -9.0139501581334991e-05
           }
          ]
         }
        ]
       }
      }
     ]
    },
    {
     "name": "right",
     "isDisplay": true,
     "isEnabled": true,
     "deviceParams": [
      {
       "key": "basic",
       "type": "arrayParam",
       "arrayParam": {
        "params": [
         {
          "key": "y",
          "type": "double",
          "doubleValue": 0.00065456142468817147
         }
        ]
       }
      },
      {
       "key": "func",
       "type": "comboParam",
       "comboParam": {
        "childParams": [
         {
          "key": "walk",
          "params": [
           {
            "key": "wheelRadius",
            "type": "double",
            "doubleValue": -3.5082871436345475e-05
           }
          ]
         }
        ]
       }
      }
     ]
    }
   ]
  },
  {
   "name": "controller",
   "devices": [
    {
     "name": "controller",
     "isDisplay": true,
     "isEnabled": true,
     "deviceParams": [
      {
       "key": "basic",
       "type": "arrayParam",
       "arrayParam": {
        "params": [
         {
          "key": "x",
          "type": "double",
          "doubleValue": 0.012987861944028789
         },
         {
          "key": "y",
          "type": "double",
          "doubleValue": 0.031189515167426907
         },
         {
          "key": "qw",
          "type": "double",
          "doubleValue": -0.018282308999170445
         },
         {
          "key": "qx",
          "type": "double",
          "doubleValue": -0.024093945493872844
         },
         {
          "key": "qy",
          "type": "double",
          "doubleValue": 0.9995421288756382
         },
         {
          "key": "qz",
          "type": "double",
          "doubleValue": 0.00087839111681940643
         },
         {
          "key": "SSF",
          "type": "double",
          "doubleValue": 0.24488080942402846
         },
         {
          "key": "Bax",
          "type": "double",
          "doubleValue": 0.067786139702906631
         },
         {
          "key": "Bay",
          "type": "double",
          "doubleValue": 0.10252454616032565
         },
         {
          "key": "Baz",
          "type": "double",
          "doubleValue": 0.4834176084041843
         }
        ]
       }
      }
     ]
    }
   ]
  },
  {
   "name": "pgv",
   "devices": [
    {
     "name": "pgvup",
     "isDisplay": true,
     "isEnabled": true,
     "deviceParams": [
      {
       "key": "basic",
       "type": "arrayParam",
       "arrayParam": {
        "params": [
         {
          "key": "x",
          "type": "double",
          "doubleValue": 0.000472
         },
         {
          "key": "y",
          "type": "double",
          "doubleValue": 0.001025
         },
         {
          "key": "yaw",
          "type": "double",
          "doubleValue": 1.240766
         }
        ]
       }
      }
     ]
    },
    {
     "name": "pgv-up",
     "isDisplay": true,
     "isEnabled": true,
     "deviceParams": [
      {
       "key": "basic",
       "type": "arrayParam",
       "arrayParam": {
        "params": [
         {
          "key": "x",
          "type": "double",
          "doubleValue": 0.00047165734067830333
         },
         {
          "key": "y",
          "type": "double",
          "doubleValue": 0.001025132894891718
         },
         {
          "key": "yaw",
          "type": "double",
          "doubleValue": 1.2407655196108323
         }
        ]
       }
      }
     ]
    }
   ]
  }
 ]
}
