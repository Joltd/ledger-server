import {Component, Input, OnInit} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Descriptor, FieldType} from "../../model/descriptor";
import {TypeUtils} from "../../../core/type-utils";
import {Reference} from "../../model/reference";
import {CollectionViewer, DataSource} from "@angular/cdk/collections";
import {BehaviorSubject, Observable} from "rxjs";
import {PageEvent} from "@angular/material/paginator";
import {FilterConfig, FilterExpression, FilterExpressionType, LoadConfig, OperatorType} from "../../model/load-config";
import {DatePipe, DecimalPipe} from "@angular/common";
import {TranslocoService} from "@ngneat/transloco";
import {OverlayService} from "../../service/overlay.service";
import {BrowserSettingsComponent} from "../browser-settings/browser-settings.component";
import {OverlayCommand} from "../../model/overlay-command";

@Component({
  selector: 'browser',
  templateUrl: './browser.component.html',
  styleUrls: ['./browser.component.scss']
})
export class BrowserComponent implements OnInit {

  @Input()
  reference!: Reference

  private decimalPipe: DecimalPipe
  private datePipe: DatePipe

  descriptor!: Descriptor
  dataSource: RemoteDateSource = new RemoteDateSource()
  config: LoadConfig = new LoadConfig()
  private columns: string[] = []

  constructor(
    private http: HttpClient,
    private translocoService: TranslocoService,
    private overlayService: OverlayService
  ) {
    let lang = this.translocoService.getActiveLang()
    this.decimalPipe = new DecimalPipe(lang)
    this.datePipe = new DatePipe(lang)

    this.setupCommonCommands()

    this.config.filter = {
      expression: {
        type: FilterExpressionType.OR,
        expressions: [
          {
            type: FilterExpressionType.AND,
            expressions: [
              {
                reference: 'length',
                operator: OperatorType.GREATER,
                value: '20',
                type: FilterExpressionType.STATEMENT
              }
            ]
          } as FilterExpression,
          {
            reference: 'time',
            operator: OperatorType.LESS,
            value: '2021-08-01T12:00:00',
            type: FilterExpressionType.STATEMENT
          },
          {
            reference: 'person.name',
            operator: OperatorType.LIKE,
            value: 'Тинь%',
            type: FilterExpressionType.STATEMENT
          },
          {
            reference: 'length',
            operator: OperatorType.EQUAL,
            value: '15',
            type: FilterExpressionType.STATEMENT
          }
        ]
      } as FilterExpression
    } as FilterConfig
  }

  ngOnInit(): void {
    this.config.page.size = 50
    this.loadDescriptor()
    this.loadData()
  }

  private loadDescriptor() {
    this.http.get<Descriptor>(this.reference.api + '/descriptor', TypeUtils.of(Descriptor))
      .subscribe(result => {
        this.descriptor = result
        this.columns = this.descriptor.dto.fields.map(entry => entry.reference);
      })
  }

  private loadData() {
    this.http.post<number>(this.reference.api + '/count', this.config)
      .subscribe(result => {
        this.config.page.length = result
        this.http.post<any[]>(this.reference.api + '/', this.config)
          .subscribe(result => this.dataSource.setData(result))
      })
  }

  allowedColumns(): string[] {
    let columns = this.columns.slice();
    columns.push('action')
    return columns
  }

  format(value: string, type: FieldType, format: string): string {
    if (type == 'NUMBER') {
      return this.decimalPipe.transform(value, format)!
    } else if (type == 'DATE') {
      return this.datePipe.transform(value, format)!
    } else {
      return value
    }
  }

  onPageChanged(event: PageEvent) {
    this.config.page.index = event.pageIndex
    this.loadData()
  }

  onSortChanged(event: any) {
    if (event.direction) {
      this.config.sort.reference = event.active
      this.config.sort.order = event.direction.toUpperCase()
    } else {
      this.config.sort.reference = undefined
    }
    this.loadData()
  }

  // toolbar

  private setupCommonCommands() {
    this.overlayService.setupCommands([
      new OverlayCommand('', 'settings', () => this.openSettings())
    ])
  }

  private setupSettingsCommand(component: BrowserSettingsComponent) {
    this.overlayService.setupCommands([
      new OverlayCommand('', 'check', () => this.saveSettings(component)),
      new OverlayCommand('', 'close', () => this.closeSettings())
    ])
  }

  private openSettings() {
    this.overlayService.openSide(BrowserSettingsComponent)
      .subscribe(component => {
        this.setupSettingsCommand(component)
        component.setup(this.reference, this.descriptor, this.columns, this.config.filter.expression)
      })
  }

  private saveSettings(component: BrowserSettingsComponent) {
    this.columns = component.columns
    this.config.filter.expression = component.filterExpression()
    this.closeSettings()
    // this.loadData()
  }

  private closeSettings() {
    this.setupCommonCommands()
    this.overlayService.closeSide()
  }

}

export class RemoteDateSource implements DataSource<any> {

  private subject: BehaviorSubject<any[]> = new BehaviorSubject<any[]>([])

  setData(data: any[]) {
    this.subject.next(data)
  }

  connect(collectionViewer: CollectionViewer): Observable<any[]> {
    return this.subject.asObservable()
  }

  disconnect(collectionViewer: CollectionViewer): void {
    this.subject.complete()
  }

}
