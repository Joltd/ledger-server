import { Component, OnInit } from '@angular/core';
import {FilterExpression} from "../../../platform/model/load-config";
import {FilterField} from "../../../platform/model/browser-provider";

@Component({
  selector: 'turnover-report-settings',
  templateUrl: './turnover-report-settings.component.html',
  styleUrls: ['./turnover-report-settings.component.scss']
})
export class TurnoverReportSettingsComponent implements OnInit {

  fromDate!: string
  toDate!: string
  filter: FilterExpression[] = []
  filterFields: FilterField[] = []

  constructor() { }

  ngOnInit(): void {}

  setup(fromDate: string, toDate: string, filterFields: FilterField[], filterExpression?: FilterExpression) {
    this.fromDate = fromDate
    this.toDate = toDate
    this.filterFields = filterFields
    this.filter = filterExpression ? [filterExpression] : []
  }

  public getFromDate(): string {
    return this.fromDate
  }

  public getToDate(): string {
    return this.toDate
  }

  public filterExpression(): FilterExpression {
    return this.filter[0]
  }

}
