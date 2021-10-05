import {Node} from "./node";
import {Edge} from "./edge";
export class Graph {

  private nodes: Node[];
  private edges: Edge[];
  

  constructor(nodes:any[], edges:any[]){
      this.initialiseGraph(nodes, edges);

  }

  private initialiseGraph(nodes:any[], edges:any[]){
      console.log("Initialising graph!")
      let tempNodes = [];
      let tempEdges = [];
    for(let i = 0; i<nodes.length; i++){
        let node = nodes[i];
        let nodeObject = new Node(node.id, node.name, node.label);
        tempNodes.push(nodeObject);
    }

    for(let i = 0; i<edges.length; i++){
        let edge = edges[i];
        // edge JSON Oject has source and target as attributes instead of successor/predecessor
        let predecessor = edge.source;
        let successor = edge.target;
        let predecessorNode = this.retrieveNodeById(predecessor, tempNodes);
        let successorNode = this.retrieveNodeById(successor, tempNodes);
        let outputEdge = new Edge(predecessor,successor,predecessorNode,successorNode);
        successorNode.addIncomingEdge(outputEdge);
        predecessorNode.addOutgoingEdge(outputEdge);
        tempEdges.push(outputEdge);
    }
    this.nodes = tempNodes;
    this.edges = tempEdges;
    console.log("Graph initiliased!")



  }

  private retrieveNodeById(id: string, nodes: Node[]): Node{
    for(let i = 0; i<nodes.length; i++){
        let node = nodes[i];
        if(node.nodeEqualsToId(id)){
            return node;
        }
    }
    console.log("No node has been found in the given list!")
    return undefined;

  }

  public getNodes(){
      return this.nodes;
  }

  public getEdges(){
      return this.edges;
  }

  public containsEdge(edge: Edge): boolean{
      for(let i = 0; i<this.edges.length; i++){
        let edgeOfGraph = this.edges[i] as Edge;
        if(edgeOfGraph.isTheSameEdge(edge)){
            return true;
        }

      }
      return false;
  }

  public containsNode(node: Node): boolean{
    for(let i = 0; i<this.nodes.length; i++){
      let nodeOfGraph = this.nodes[i] as Node;
      if(nodeOfGraph.isTheSameNode(node)){
          return true;
      }

    }
    return false;
}




}