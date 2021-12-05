import {Component, Input, OnInit} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Descriptor, FieldType} from "../../model/descriptor";
import {TypeUtils} from "../../../core/type-utils";
import {Reference} from "../../model/reference";
import {CollectionViewer, DataSource} from "@angular/cdk/collections";
import {BehaviorSubject, Observable} from "rxjs";
import {PageEvent} from "@angular/material/paginator";
import {FilterConfig, FilterExpression, LoadConfig} from "../../model/load-config";
import {CurrencyPipe, DatePipe, DecimalPipe} from "@angular/common";
import {TranslocoService} from "@ngneat/transloco";

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

  constructor(
    private http: HttpClient,
    private translocoService: TranslocoService
  ) {
    let lang = this.translocoService.getActiveLang()
    this.decimalPipe = new DecimalPipe(lang)
    this.datePipe = new DatePipe(lang)

    this.config.filter = {
      expression: {
        type: 'OR',
        expressions: [
          {
            reference: 'time',
            operator: 'LESS',
            value: '2021-08-01T12:00:00',
            type: 'STATEMENT'
          },
          {
            reference: 'person.name',
            operator: 'LIKE',
            value: 'Тинь%',
            type: 'STATEMENT'
          },
          {
            reference: 'length',
            operator: 'EQUAL',
            value: '15',
            type: 'STATEMENT'
          }
        ]
      }
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

  columns(): string[] {
    let columns = this.descriptor.dto.fields.map(entry => entry.reference);
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
