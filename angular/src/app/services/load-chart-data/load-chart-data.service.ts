import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LoadChartDataService {

  constructor(private http: HttpClient) {}

  private messageSource = new BehaviorSubject<any>(null);
  chartMessage = this.messageSource.asObservable();

  nextMessage(message: any) {
    this.messageSource.next(message)
  }

  private graphData = {
    inited: false,
    nodes: [],
    links: []
  };

  public async getGraphData() {
    if (!this.graphData.inited) {
      console.log("fetching data");
      let dataBatches = await this.getNodes();
      let dataBatchGraph = await this.getEdges();
      this.constructGraph(dataBatches, dataBatchGraph);
    }
    return this.graphData;
  }

  private async getNodes() {
    const respNodes = await this.http.get("/assets/batch.csv", { responseType: 'text' }).toPromise();
    const list = respNodes.split('\n');
    let nodes = new Map();
    list.forEach((e, i) => {
      if (i > 0) {
        var parts = e.split(',');
        var batch = { id: parts[0], name: parts[1], shortName: parts[2] };
        if (batch.id != "") {
          nodes.set(batch.id, batch);
        }
      }
    });
    return nodes;
  }

  private async getEdges() {
    const respEdges = await this.http.get("/assets/batchgraph.csv", { responseType: 'text' }).toPromise();
    const list = respEdges.split('\n');
    let edges = [];
    list.forEach((e, i) => {
      if (i > 0) {
        var parts = e.split(','); if (parts.length > 1) {
          var edge = { predecessor: parts[0].trim(), successor: parts[1].trim() };
          edges.push(edge);
        }
      }
    });
    return edges;
  }

  private constructGraph(nodes: Map<String, any>, edges: any[]) {
    nodes.forEach((value: any, key: string) => {
      this.graphData.nodes.push({ id: key, name: value.name, label: value.shortName });
    });

    edges.forEach((e) => {
      this.graphData.links.push({ source: e.predecessor, target: e.successor, type: 'Next -->>' });
    });

    this.graphData.inited=true;
  }
}
