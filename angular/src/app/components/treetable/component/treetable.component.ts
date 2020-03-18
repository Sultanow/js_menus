import { Component, OnInit, Input, Output, ElementRef, TemplateRef } from '@angular/core';
import { Node, TreeTableNode, Options, SearchableNode } from '../models';
import { TreeService } from '../services/tree/tree.service';
import { MatTableDataSource } from '@angular/material/table';
import { ValidatorService } from '../services/validator/validator.service';
import { ConverterService } from '../services/converter/converter.service';
import { defaultOptions } from '../default.options';
import * as _ from 'lodash';
import { Required } from '../decorators/required.decorator';
import { Subject } from 'rxjs';

@Component({
  selector: 'treetable',
  templateUrl: './treetable.component.html',
  styleUrls: [ './treetable.component.css' ]
})
export class TreetableComponent<T> implements OnInit {
  @Input() @Required tree: Node<T> | Node<T>[];
  @Input() options: Options<T> = {};
  @Output() nodeClicked: Subject<TreeTableNode<T>> = new Subject();
  @Input('body') customElement: TemplateRef<any>; //custom template injection
  @Input('body_key') customElementKey: TemplateRef<any>;


  private searchableTree: SearchableNode<T>[];
  private treeTable: TreeTableNode<T>[];
  displayedColumns: string[];
  dataSource: MatTableDataSource<TreeTableNode<T>>;

  constructor (
    private treeService: TreeService,
    private validatorService: ValidatorService,
    private converterService: ConverterService,
    elem: ElementRef
  ) {
    const tagName = elem.nativeElement.tagName.toLowerCase();
    if (tagName === 'ng-treetable') {
      console.warn(`DEPRECATION WARNING: \n The 'ng-treetable' selector is being deprecated. Please use the new 'treetable' selector`);
    }
  }

  ngOnInit() {
    this.tree = Array.isArray(this.tree) ? this.tree : [ this.tree ];
    if (this.tree.length != 0) {
      this.options = this.parseOptions(defaultOptions);
      const customOrderValidator = this.validatorService.validateCustomOrder(this.tree[ 0 ], this.options.customColumnOrder);
      if (this.options.customColumnOrder && !customOrderValidator.valid) {
        throw new Error(`
        Properties ${customOrderValidator.xor.map(x => `'${x}'`).join(', ')} incorrect or missing in customColumnOrder`
        );
      }
      this.displayedColumns = this.options.customColumnOrder
        ? this.options.customColumnOrder
        : this.extractNodeProps(this.tree[ 0 ]);
      this.searchableTree = this.tree.map(t => this.converterService.toSearchableTree(t));
      const treeTableTree = this.searchableTree.map(st => this.converterService.toTreeTableTree(st));
      this.treeTable = _.flatMap(treeTableTree, this.treeService.flatten);
      this.dataSource = this.generateDataSource();
      this.dataSource.filterPredicate = (data, filter: string) => {
        const accumulator = (currentTerm, key) => {
          return this.nestedFilterCheck(currentTerm, data, key);
        };
        const dataStr = Object.keys(data).reduce(accumulator, '').toLowerCase();
        // Transform the filter by converting it to lowercase and removing whitespace.
        const transformedFilter = filter.trim().toLowerCase();
        return dataStr.indexOf(transformedFilter) !== -1;
      };
    }
  }

  nestedFilterCheck(search, data, key) {
    if (typeof data[ key ] === 'object') {
      for (const k in data[ key ]) {
        if (data[ key ][ k ] !== null) {
          search = this.nestedFilterCheck(search, data[ key ], k);
        }
      }
    } else {
      search += data[ key ];
    }
    return search;
  }

  extractNodeProps(tree: Node<T> & { value: { [ k: string ]: any; }; }): string[] {
    return Object.keys(tree.value);//.filter(x => typeof tree.value[x] !== 'object');
  }

  generateDataSource(): MatTableDataSource<TreeTableNode<T>> {
    return new MatTableDataSource(this.treeTable.filter(x => x.isVisible));
  }

  formatIndentation(node: TreeTableNode<T>, step: number = 5): string {
    return '&nbsp;'.repeat(node.depth * step);
  }

  formatElevation(): string {
    return `mat-elevation-z${this.options.elevation}`;
  }

  onNodeClick(clickedNode: TreeTableNode<T>): void {
    clickedNode.isExpanded = !clickedNode.isExpanded;
    this.treeTable.forEach(el => {
      el.isVisible = this.searchableTree.every(st => {
        return this.treeService.searchById(st, el.id).
          fold([], n => n.pathToRoot)
          .every(p => this.treeTable.find(x => x.id === p.id).isExpanded);
      });
    });
    this.dataSource = this.generateDataSource();
    this.nodeClicked.next(clickedNode);
  }

  // Overrides default options with those specified by the user
  parseOptions(defaultOpts: Options<T>): Options<T> {
    return _.defaults(this.options, defaultOpts);
  }

  get GetElement() {
    return {
      // element = this.
    };
  }

  applyFilter(filterValue: string) {
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

}
