import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {FilterExpression, FilterExpressionType, OperatorType} from "../../model/load-config";
import {NestedTreeControl} from "@angular/cdk/tree";
import {MatTreeNestedDataSource} from "@angular/material/tree";
import {BrowserProvider, FilterField, RowField} from "../../model/browser-provider";

@Component({
  selector: 'browser-settings',
  templateUrl: './browser-settings.component.html',
  styleUrls: ['./browser-settings.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class BrowserSettingsComponent implements OnInit {

  browserProvider!: BrowserProvider
  columns: string[] = []

  private filter: FilterExpression[] = []
  treeControl: NestedTreeControl<FilterExpression> = new NestedTreeControl<FilterExpression>(node => node.expressions)
  dataSource: MatTreeNestedDataSource<FilterExpression> = new MatTreeNestedDataSource<FilterExpression>()
  filterFields: FilterField[] = []
  rowFields: RowField[] = []
  operators: OperatorType[] = Object.values(OperatorType)
  types: FilterExpressionType[] = Object.values(FilterExpressionType)
  structureTypes: FilterExpressionType[] = this.types.filter(type => type != FilterExpressionType.STATEMENT)

  constructor() {}

  ngOnInit(): void {}

  setup(browserProvider: BrowserProvider, columns: string[], filterExpression?: FilterExpression) {
    this.browserProvider = browserProvider
    this.columns = columns
    this.filter = filterExpression ? [filterExpression] : []
    this.filterFields = this.browserProvider.filterModel()
    this.updateFilterDataSource()
    this.rowFields = this.browserProvider.rowModel()
  }

  hasChild(index: number, node: FilterExpression) {
    return node.expressions?.length > 0
  }

  public filterExpression(): FilterExpression {
    return this.filter[0]
  }

  private updateFilterDataSource() {
    this.dataSource.data = []
    this.dataSource.data = this.filter
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

}
