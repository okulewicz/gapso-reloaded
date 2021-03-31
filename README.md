# GAPSO

Generalized Adaptive Particle Swarm Optimization.
The framework applies a population based optimizer,
which consists of the following modules:

 * Bounds manager - selects
 function bounds within which the function optimum is sought
 * Initializer - generates initial locations of the population
 within set bounds
 * Restart manager - decides if the population needs to be restarted
 and optimization process started again (possibly within different bounds
 or with different initialization strategy)
 * MoveManager - sampler of possible moves
 * Moves - the actual optimizers

## General settings (gapso.json)

```json
{
  "seed": 1,
  "particlesCountPerDimension": 10,
  "evaluationsBudgetPerDimension": 100000,
  "splitBounds": false,
  "boundsManagerDefinition": {},
  "initializerDefinition": {},
  "restartManagerDefinition": {},
  "moveManagerDefinition": {}
}
```

## Bounds managers
  
  * ResetAll - Always start with full function bounds
```json
{
  "name": "ResetAll"
}
``` 
  * GlobalModel - Tries to guess bounds from global model optimum estimations
```json
{
  "name": "GlobalModel"
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
  of best samples is less then threshold times absolute value of the function
  (taken from R-SHADE)
```json
{
  "name": "FunctionValues",
  "parameters": {
    "threshold": 1e-8
  }
}
```
  * MinSpreadInDimensions - restarts swarm if difference of the best locations
  in at least one direction is smaller than threshold times absolute value
  of the location (taken from R-SHADE)
  * MaxSpreadInDimensions - restarts swarm if difference of the best locations
  in each of the directions is smaller than threshold 
  * NoImprovement - restarts swarm if for a certain amount of evaluations (not iterations)
  there was no improvement in the estimated global optimum value
```json
{
  "name": "NoImprovement",
  "parameters": {
    "evaluationsPerDimensionLimit": 5000
  }
}
```
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

## MovesManager  

Moves manager decides what to do with moves weights and adapts them on the basis of improvements
made by particular moves

```json
{
    "adaptMoves": false,
    "maxHistorySize": 0,
    "includePersonalImprovements": false,
    "includeGlobalImprovements": false,
    "switchingAdaptationOffProbability": 0.0,
    "moves": []
}

```

## Moves

  * DE/best/1/bin -
  Standard DE with possibilty of randomizing scale and crossProb for each move
```json
{
  "name": "DE/best/1/bin",
  "isAdaptable": true,
  "initialWeight": 1000,
  "minimalAmount": 1,
  "minimalRatio": 0.0,
  "parameters": {
    "crossProb": 0.5,
    "scale": 1.2,
    "constantCrossProb": true,
    "constantScale": false
  }
}
```

  * SHADE (without decreasing population size)
  
```json
  {
    "name": "SHADE",
    "isAdaptable": true,
    "initialWeight": 3000,
    "minimalAmount": 1,
    "parameters": {
      "crossProb": 0.9,
      "scale": 0.6,
      "pBestRatio": 0.11,
      "archiveSizeFactor": 2.0,
      "slots": 6
    }
  }
```

  * CMA-ES (copied from Apache Math packages and tailored to GAPSO framework)
  
```json
{
  "name": "CMAESApache",
  "isAdaptable": true,
  "initialWeight": 3000,
  "minimalRatio": 0.2,
  "minimalAmount": 4,
  "parameters": {
    "followCurrentBest": false,
    "followGlobalBest": false,
    "minIterationsBeforeFollow": 10,
    "followToleranceFactor": 4.0
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
      "minimalRatio": 0.05,
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
  "minimalRatio": 0.05,
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
    ],
    "clusteringType": "NONE|LARGEST|BEST"
  }
}
```

  * NearestSamples - tries to apply the first model with enough samples and current availability
  model is fitted on subset of samples nearby to currently selected particles' best

```json
{
  "name": "NearestSamples",
  "isAdaptable": false,
  "initialWeight": 0,
  "minimalAmount": 2,
  "minimalRatio": 0.1,
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
    ],
    "clusteringType": "NONE|LARGEST|BEST"
  }
}
```

