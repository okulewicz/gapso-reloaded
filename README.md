# gapso-reloaded
Generalized Adaptive Particle Swarm Optimization

## Moves

  * DE/best/1/bin
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
  * LocalBestModel
  
## Restart Managers

  * FunctionValues
  * MinSpreadInDimensions
  * MaxSpreadInDimensions 
 
## Initializers
  
  * Sequence  
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
  * Model
  * Random
