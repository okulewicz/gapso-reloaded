# gapso-reloaded
Generalized Adaptive Particle Swarm Optimization

## General settings (gapso.json)

```json
{
  "seed": 1,
  "particlesCountPerDimension": 10,
  "evaluationsBudgetPerDimension": 100000,
  "splitBounds": false,
  "initializerDefinition": {},
  "restartManagerDefinition": {},
  "moveDefinition": []
}
```

## Initializers
  
  * Sequence - tries to apply first available initializer
```json
{
  "name": "Sequence",
  "parameters": {
    "initializers": [
      {
        "name": "Model"
      },
      {
        "name": "Random"
      }
    ]
  }
}
```
  * Model - initializes location on the basis of all gathered samples
  * Random - random sample within given bounds
  
## Restart Managers

  * FunctionValues - restarts swarm when difference in function values
  of best samples is less then threshold
```json
{
  "name": "FunctionValues",
  "parameters": {
    "threshold": 1e-8
  }
}
```
  * MinSpreadInDimensions
  * MaxSpreadInDimensions 
  * Or - restarts when at least one rule is met
  ```json
{
"name": "Or",
"parameters": {
  "restartManagerDefinitions": [
    {
      "name": "FunctionValues",
      "parameters": {
        "threshold": 1e-8
      }
    },
    {
      "name": "MaxSpreadInDimensions",
      "parameters": {
        "threshold": 1e-8
      }
    }

  ]
}
}
```
  * And

## Moves

  * DE/best/1/bin -
  Standard DE with possibilty of randomizing scale and crossProb for each move
```json
{
  "name": "DE/best/1/bin",
  "isAdaptable": true,
  "initialWeight": 1000,
  "minimalAmount": 1,
  "parameters": {
    "crossProb": 0.5,
    "scale": 1.2,
    "constantCrossProb": true,
    "constantScale": false
  }
}
```
  * LocalBestModel - tries to apply the first model with enough samples and current availability
  model is fitted on particles' bests

```json
{
      "name": "LocalBestModel",
      "isAdaptable": true,
      "initialWeight": 500,
      "minimalAmount": 1,
      "parameters": {
        "models": [
        {
          "modelType" : "FullSquare",
          "modelUseFrequency": 20
        },
        {
          "modelType" : "SimpleSquare",
          "modelUseFrequency": 1
        }
        ]
      }
}
```

  * GlobalModel - tries to apply the first model with enough samples and current availability
  model is fitted on subset of all samples gathered so far

```json
{
  "name": "GlobalModel",
  "isAdaptable": false,
  "initialWeight": 0,
  "minimalAmount": 2,
  "parameters": {
    "models": [
      {
        "modelType" : "FullSquare",
        "modelUseFrequency": 20
      },
      {
        "modelType" : "SimpleSquare",
        "modelUseFrequency": 1
      }
    ]
  }
}
```


