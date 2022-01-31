import {Component, Input, OnInit} from '@angular/core';
import {MatTreeNestedDataSource} from "@angular/material/tree";
import {FilterExpression, FilterExpressionType, OperatorType} from "../../model/load-config";
import {NestedTreeControl} from "@angular/cdk/tree";
import {FilterField} from "../../model/browser-provider";

@Component({
  selector: 'filter-expression-component',
  templateUrl: './filter-expression-component.component.html',
  styleUrls: ['./filter-expression-component.component.scss']
})
export class FilterExpressionComponentComponent implements OnInit {

  private _filter: FilterExpression[] = []

  @Input()
  set filter(filter: FilterExpression[]) {
    this._filter = filter
    this.updateFilterDataSource()
  }

  get filter(): FilterExpression[] {
    return this._filter
  }

  @Input()
  filterFields: FilterField[] = []

  dataSource: MatTreeNestedDataSource<FilterExpression> = new MatTreeNestedDataSource<FilterExpression>()
  treeControl: NestedTreeControl<FilterExpression> = new NestedTreeControl<FilterExpression>(node => node.expressions)
  operators: OperatorType[] = Object.values(OperatorType)
  types: FilterExpressionType[] = Object.values(FilterExpressionType)
  structureTypes: FilterExpressionType[] = this.types.filter(type => type != FilterExpressionType.STATEMENT)

  constructor() { }

  ngOnInit(): void {
  }

  hasChild(index: number, node: FilterExpression) {
    return node.expressions?.length > 0
  }

  addExpression(expression: FilterExpression | null, type: FilterExpressionType) {
    let newExpression = new FilterExpression();
    newExpression.type = type
    newExpression.expressions = []
    if (expression != null) {
      expression.expressions.push(newExpression)
    } else {
      this.filter = [newExpression]
    }
    this.updateFilterDataSource()
  }

  removeExpression(expression: FilterExpression) {
    let root = this.filter[0]
    if (!root) {
      return
    }

    if (expression == root) {
      this.filter = []
      this.updateFilterDataSource()
      return
    }

    let parent = this.seekParent(root, expression)
    if (!parent) {
      return
    }

    parent.expressions = parent.expressions.filter(entry => entry != expression)
    this.updateFilterDataSource()
  }

  wrapExpression(expression: FilterExpression, type: FilterExpressionType) {
    let newExpression = new FilterExpression();
    newExpression.type = type
    newExpression.expressions = [expression]

    let root = this.filter[0]
    if (!root) {
      return
    }

    if (expression == root) {
      this.filter = [newExpression]
      this.updateFilterDataSource()
      this.treeControl.expand(newExpression)
      return
    }

    let parent = this.seekParent(root, expression)
    if (!parent) {
      return
    }

    let index = parent.expressions.indexOf(expression)
    parent.expressions[index] = newExpression
    this.updateFilterDataSource()
    this.treeControl.expand(newExpression)
  }

  private seekParent(current: FilterExpression, target: FilterExpression): FilterExpression | null {
    for (let sub of current.expressions) {
      if (sub == target) {
        return current
      }
      if (sub.expressions?.length > 0) {
        let result = this.seekParent(sub, target)
        if (result) {
          return result
        }
      }
    }
    return null
  }

  private updateFilterDataSource() {
    this.dataSource.data = []
    this.dataSource.data = this.filter
  }

}
