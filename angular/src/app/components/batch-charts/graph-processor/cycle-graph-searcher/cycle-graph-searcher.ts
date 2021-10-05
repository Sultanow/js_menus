import {Node} from "../graph/node";
import {Graph} from "../graph/graph";
import { Edge } from "../graph/edge";
export class CycleGraphSearcher{
      private graph: Graph;
      private cycles = [];
      private whiteNodeSet = []; // unvisited Nodes
      private blackNodeSet = []; // fully visited Nodes
      private directCircularDependenciesNode = [];

      private nodesInCycles: Set<Node> = new Set<Node>();
      private edgesInCycles: Set<Edge> = new Set<Edge>();

    constructor(graphDataObject: Graph){
        this.graph = graphDataObject;
        console.log("Cycle detection in graph started!")
    }

    private setUpNodesToBeChecked(nodes: Node[]){
      // Not reviewing nodes with no successors or no predecessors as they cannot be a part of cycle!
      for(let i = 0; i<nodes.length; i++){
        let node = nodes[i];
        if(node.getIncomingEdges().length!=0 && node.getOutgoingEdges().length != 0){
            this.whiteNodeSet.push(node);
        }
      }
        console.log("Amount of nodes to be checked --> " + this.whiteNodeSet.length);

    }

    private checkForDirectCircularDependency(){
      //Checking for nodes that have a direct circular dependency as this should not be allowed in our Use Case (Example: A-->B and B-->A)
      for(let i = 0; i<this.whiteNodeSet.length; i++){
        let node = this.whiteNodeSet[i] as Node;
        for(let j = 0; j<node.getOutgoingEdges().length; j++){
          let edge = node.getOutgoingEdges()[j];
          let nextNode = edge.getSuccessorNode() as Node;
          for(let k = 0; k<nextNode.getOutgoingEdges().length; k++){
            if(node.isTheSameNode(nextNode.getOutgoingEdges()[k].getSuccessorNode())){
              console.log("WARNING: Direct Circular Dependency Found!")
              let directDependentNodes = [];
              directDependentNodes.push(node);
              directDependentNodes.push(nextNode);
              this.addDirectDependencyNodes(directDependentNodes);
            }
          }
        }
      }
    }

    private addDirectDependencyNodes(dependencyPair: Node[]){
              let samePairFound = false;
              for(let i = 0; i<this.directCircularDependenciesNode.length; i++){
                if(this.isTheSameDependencyPair(this.directCircularDependenciesNode[i], dependencyPair)){
                  samePairFound = true;
                  break;
                }
              }
              if(samePairFound){
                return;
              }
              this.directCircularDependenciesNode.push(dependencyPair);
            }
    

    private isTheSameDependencyPair(nodePair1: Node[], nodePair2: Node[]):boolean{
      let nodeOneOfPairOne = nodePair1[0] as Node;
      let nodeTwoOfPairOne = nodePair1[1] as Node;
      let nodeOneOfPairTwo = nodePair2[0] as Node;
      let nodeTwoOfPairTwo = nodePair2[1] as Node;

      return (nodeOneOfPairOne.isTheSameNode(nodeTwoOfPairTwo) && nodeTwoOfPairOne.isTheSameNode(nodeOneOfPairTwo)) || (nodeOneOfPairOne.isTheSameNode(nodeOneOfPairTwo) && nodeTwoOfPairOne.isTheSameNode(nodeTwoOfPairTwo));
    }


    public startSearchingForCycles(){
      this.setUpNodesToBeChecked(this.graph.getNodes());
      this.checkForDirectCircularDependency();
      this.whiteNodeSet.forEach(node =>{
        // Do Depth First Search.
          let graySet = [];
          this.depthFirstSearchRecursive(node, graySet);
          let index  = this.whiteNodeSet.indexOf(node, 0);
          this.whiteNodeSet.splice(index,1);
          this.blackNodeSet.push(node);
      });
      
    }

    

    public depthFirstSearchRecursive(node: Node, visitedNodes: Node[]){
      visitedNodes.push(node);
      for(let i = 0; i < node.getOutgoingEdges().length; i++){
        let nextNode = node.getOutgoingEdges()[i].getSuccessorNode() as Node;
        if(visitedNodes.includes(nextNode)){
          //Cycle detetcted
          this.markAllElementsOfCycle(visitedNodes, nextNode, node);
          continue;
        }
        if(this.whiteNodeSet.includes(nextNode) && !this.blackNodeSet.includes(nextNode)){
          this.depthFirstSearchRecursive(nextNode, visitedNodes);
        }
    }
        let i = visitedNodes.indexOf(node,0);
        visitedNodes.splice(i,1);
  }

      private markAllElementsOfCycle(cycleSet: Node[], startNode: Node, endNode: Node){
        let cycle = [];
        let inCycle = false;
        for(let i = 0; i<cycleSet.length; i++){
          if(cycleSet[i].isTheSameNode(startNode)){
            inCycle = true;
          } else if(cycleSet[i].isTheSameNode(endNode)){
            inCycle = false;
            cycle.push(cycleSet[i]);
            break;
          }
          if(inCycle){
            cycle.push(cycleSet[i]);
          }
        }
        this.printCycle(cycle);
        this.cycles.push(cycle);

        for(let i = 0; i< cycle.length; i++){
          let currentNode = cycle[i] as Node;
          currentNode.setNodeInCycle(true);
          let nextNodeInCycle;
          if(cycle.length == i+1){
            nextNodeInCycle = cycle[0] as Node;
          }else{
            nextNodeInCycle = cycle[i+1] as Node;
            }
            currentNode.getOutgoingEdges().forEach(edge => {
              if(edge.getSuccessorNode().isTheSameNode(nextNodeInCycle)){
                edge.setEdgeInCycle(true);
                this.edgesInCycles.add(edge);
              }
            });
            this.nodesInCycles.add(currentNode);
        }
      }

      public updateGraphWithInCycleData(graphData: any): any {
      this.nodesInCycles.forEach(node => {
        for(let i = 0; i<graphData.nodes.length; i++){
          let nodeOfGraph = graphData.nodes[i];
          if(node.isSameIdOfNode(nodeOfGraph.id)){
            graphData.nodes[i] = {id:node.getId(), name:node.getName(), label:node.getShortName(), inCycle:node.isNodeInCycle(), updateStatus:node.getUpdateStatus()};
          }
        }
        
      })
      this.edgesInCycles.forEach(edge => {
        for(let i = 0; i<graphData.links.length; i++){
          let edgeOfGraph = graphData.links[i];
          if(edge.isTheSameEdgePerSuccessorPredecessor(edgeOfGraph.source,edgeOfGraph.target)){
            graphData.links[i] = {source:edge.getPredecessor(), target:edge.getSuccessor(), inCycle: edge.isEdgeInCycle(), updateStatus: edge.getUpdateStatus(), type: 'Next -->>' }};
          }
        });
        let directDependndies= [];
        for(let i = 0; i<this.directCircularDependenciesNode.length; i++){
          let node1 = this.directCircularDependenciesNode[i][0];
          let node2 = this.directCircularDependenciesNode[i][1];
          let nodePairArray = [];
          nodePairArray.push({id: node1.getId(), name: node1.getName(), label: node1.getShortName(), inCycle: true, updateStatus: node1.getUpdateStatus()});
          nodePairArray.push({id: node2.getId(), name: node2.getName(), label: node2.getShortName(), inCycle: true, updateStatus: node2.getUpdateStatus()});
          directDependndies.push(nodePairArray);
        }
        graphData.directDependantNodes = directDependndies;
        return graphData;
      }

      public getDirectDependantNodes(){
        return this.directCircularDependenciesNode;
      }

      public printCycle(visitedNodes: Node[]){
        console.log("Cycle detected! Printing:")
        let output= "";
        for(let i = 0; i< visitedNodes.length; i++){
          output+=visitedNodes[i].getName() + " => " ;
        }
      output+=visitedNodes[0].getName();
      console.log(output);

      }

    


    




}