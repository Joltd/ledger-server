import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {Descriptor, MetaField} from "../../model/descriptor";
import {Reference} from "../../model/reference";
import {FilterExpression, FilterExpressionType, OperatorType} from "../../model/load-config";
import {NestedTreeControl} from "@angular/cdk/tree";
import {MatTreeNestedDataSource} from "@angular/material/tree";

@Component({
  selector: 'browser-settings',
  templateUrl: './browser-settings.component.html',
  styleUrls: ['./browser-settings.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class BrowserSettingsComponent implements OnInit {

  reference!: Reference

  descriptor!: Descriptor
  columns: string[] = []

  private filter: FilterExpression[] = []
  treeControl: NestedTreeControl<FilterExpression> = new NestedTreeControl<FilterExpression>(node => node.expressions)
  dataSource: MatTreeNestedDataSource<FilterExpression> = new MatTreeNestedDataSource<FilterExpression>()
  fields: string[] = []
  operators: OperatorType[] = Object.values(OperatorType)
  types: FilterExpressionType[] = Object.values(FilterExpressionType)
  structureTypes: FilterExpressionType[] = this.types.filter(type => type != FilterExpressionType.STATEMENT)

  constructor() {}

  ngOnInit(): void {}

  setup(reference: Reference, descriptor: Descriptor, columns: string[], filterExpression?: FilterExpression) {
    this.reference = reference
    this.descriptor = descriptor
    this.columns = columns
    this.filter = filterExpression ? [filterExpression] : []
    this.updateFilterDataSource()
    this.fields = this.extractFields(descriptor.meta.fields, '')
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

  private extractFields(metaFields: MetaField[], prefix: string): string[] {
    let fields: string[] = []
    for (let metaField of metaFields) {
      if (metaField.fields?.length > 0) {
        this.extractFields(metaField.fields, prefix + metaField.reference + '.')
          .forEach(field => fields.push(field))
      } else {
        fields.push(prefix + metaField.reference)
      }
    }
    return fields
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
