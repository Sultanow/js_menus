import {Node} from "../graph/node";
import {Graph} from "../graph/graph";
import { Edge } from "../graph/edge";
export class DifferenceGraphIdentifier {

  private currentGraphJSON = {
    inited: false,
    nodes: [],
    links: [],
    directDependantNodes:[]
  };

  private graphToCompareJSON = {
    inited: false,
    nodes: [],
    links: [],
    directDependantNodes:[]
  };
  private graphToCompareProvided: boolean;
  private currentGraph: Graph;

  private newGraphToCompare: Graph;

  
  constructor(graphDataObject: any, graphDataToCompare: any){
    this.currentGraphJSON = graphDataObject;
    this.currentGraph = new Graph(this.currentGraphJSON.nodes, this.currentGraphJSON.links);
    //Checking if graph to compare has been provided at all and it is not empty
    if(graphDataToCompare.nodes.length == 0 ||graphDataToCompare.links.length == 0){
      this.graphToCompareProvided = false;

    } else {
      this.graphToCompareProvided = true;
    }
    this.graphToCompareJSON = graphDataToCompare;
    this.newGraphToCompare = new Graph(this.graphToCompareJSON.nodes, this.graphToCompareJSON.links);
  }

public getCurrentGraph(): Graph{
  return this.currentGraph;
}
  public getDifferenceGraphNodesAndEdges(): any {
    if(!this.graphToCompareProvided){
      return this.currentGraphJSON;
    }
      //First we find nodes that are in current graph but not in graph that we compare it too --> meaning the nodes have been deleted
      let insertedNodes = this.findNodeDifferencesInGraph(this.currentGraph, this.newGraphToCompare) as Node[];
      insertedNodes.forEach(node => node.setUpdateStatusTo("deleted"));
      //Then we find edges that are in current graph but not in graph taht we compare it too --> meaning the edges have been deleted
      let insertedEdges = this.findEdgeDifferencesInGraph(this.currentGraph, this.newGraphToCompare) as Edge[];
      insertedEdges.forEach(edge => edge.setUpdateStatusTo("deleted"));
      //Second  we find nodes that are in graph to compare but not in current graph  --> meaning the nodes have been inserted
      let deletedNodes = this.findNodeDifferencesInGraph(this.newGraphToCompare, this.currentGraph) as Node[];
      deletedNodes.forEach(node => node.setUpdateStatusTo("inserted"));
      //Then  we find edge that are in graph to compare but not in current graph  --> meaning the edges have been inserted
      let deletedEdges = this.findEdgeDifferencesInGraph(this.newGraphToCompare, this.currentGraph) as Edge[];
      deletedEdges.forEach(edge => edge.setUpdateStatusTo("inserted"));
      this.addNodesToCurrentGraph(insertedNodes, deletedNodes);
      this.addEdgesToCurrentGraph(insertedEdges, deletedEdges);
      return this.currentGraphJSON;

  }

  
  private findNodeDifferencesInGraph(graphOriginal: Graph, graphToCompareTo: Graph): Node[]{
    let nodeOutput = [];
    graphOriginal.getNodes().forEach(node => {
      if(!graphToCompareTo.containsNode(node)){
        console.log("Found node difference in graph!");
        nodeOutput.push(node);
      }
    });
    return nodeOutput;
  }

  private findEdgeDifferencesInGraph(graphOriginal: Graph, graphToCompareTo: Graph): Edge[]{
    let edgeOutput = [];
    graphOriginal.getEdges().forEach(edge => {
      if(!graphToCompareTo.containsEdge(edge)){
        console.log("Found edge difference in graph!");
        edgeOutput.push(edge);
      }
    });
    return edgeOutput;
  }

  private addNodesToCurrentGraph(insertedNodes: Node[], deletedNodes: Node[] ){
    insertedNodes.forEach(node =>{
      for(let i = 0; i<this.currentGraphJSON.nodes.length; i++){
        let n = this.currentGraphJSON.nodes[i];
        if(n.id === node.getId() && n.name === node.getName()){
          this.currentGraphJSON.nodes[i].updateStatus = node.getUpdateStatus();
        }
      }
    });
    deletedNodes.forEach(node =>{
      this.currentGraphJSON.nodes.push({ id: node.getId(), name: node.getName(), label: node.getShortName(), inCycle: node.isNodeInCycle(), updateStatus: node.getUpdateStatus() });
    });

  }

  private addEdgesToCurrentGraph(insertedEdges: Edge[], deletedEdges: Edge[] ){
    insertedEdges.forEach(edge =>{
      for(let i = 0; i<this.currentGraphJSON.links.length; i++){
        let link = this.currentGraphJSON.links[i];
        if(link.source === edge.getPredecessor() && link.target === edge.getSuccessor()){
          this.currentGraphJSON.links[i].updateStatus = edge.getUpdateStatus();
        }
      }
    });
    deletedEdges.forEach(edge =>{
      this.currentGraphJSON.links.push({ source:edge.getPredecessor(), target: edge.getSuccessor(), inCycle: edge.isEdgeInCycle(), updateStatus: edge.getUpdateStatus(), type: 'Next -->>' });
    });

  }






}