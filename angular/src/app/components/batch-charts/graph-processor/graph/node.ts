import {Edge} from "./edge";
export class Node {
    private updateStatusMap = new Map([
        ["same", "same"],
        ["deleted", "deleted"],
        ["inserted", "inserted"]
    ]); 
    private id: string;
    private name: string;
    private shortName: string;
    private nodeInCycle: boolean;
    private incomingEdges: Edge[];
    private outgoingEdges: Edge[];
    private updateStatus: string;

    constructor(id:string, name:string, shortName:string){
    this.id = id;
    this.name = name;
    this.shortName = shortName;
    this.incomingEdges = [];
    this.outgoingEdges = [];
    this.updateStatus = this.updateStatusMap.get("same");
    this.nodeInCycle = false;

    }

 

    public setUpdateStatusTo(status:string){
        this.updateStatus = this.updateStatusMap.get(status);
        }

        public getUpdateStatus(): string {
            return this.updateStatus;
        }

    public getId(): string{
        return this.id;
    }

    public getName(): string{

        return this.name;
    }
    public getShortName(): string{
        return this.shortName;
    }


    public isTheSameNode(otherNode: Node): boolean{
        return ((this.id === otherNode.getId()) && (this.name === otherNode.getName()));

    }

    public nodeEqualsToId(id: string){
        return this.id == id;
    }

    public isSameIdOfNode(id:string):boolean{
        return this.id === id;
    }
    
public isNodeInCycle(): boolean{
    return this.nodeInCycle;
}

public setNodeInCycle(inCycle: boolean){
    this.nodeInCycle = inCycle;
}

public addIncomingEdge(edge: Edge){
    this.incomingEdges.push(edge);
}

public addOutgoingEdge(edge: Edge){
    this.outgoingEdges.push(edge);
}

public getIncomingEdges(){
    return this.incomingEdges;
}

public getOutgoingEdges(){
    return this.outgoingEdges;
}

public toString = () : string => {
    return `Node (id: ${this.id}, name: ${this.name})`;
}


  
  }