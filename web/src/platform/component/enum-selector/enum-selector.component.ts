import {Component, ElementRef, HostBinding, Input, OnDestroy, OnInit, Optional, Self, ViewChild} from '@angular/core';
import {MatFormFieldControl} from "@angular/material/form-field";
import {ControlValueAccessor, FormControl, NgControl} from "@angular/forms";
import {Reference} from "../../model/entity";
import {Observable, Subject} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {debounceTime, startWith, switchMap} from "rxjs/operators";
import {coerceBooleanProperty} from "@angular/cdk/coercion";
import {environment} from "../../../environments/environment";
import {TypeUtils} from "../../../core/type-utils";

@Component({
  selector: 'enum-selector',
  templateUrl: './enum-selector.component.html',
  styleUrls: ['./enum-selector.component.scss']
})
export class EnumSelectorComponent implements MatFormFieldControl<string>,ControlValueAccessor,OnDestroy {

  private static nextId: number = 0

  @Input()
  api!: string
  @Input()
  localizationKey: string = ''
  @ViewChild('input')
  input!: ElementRef
  @Input('aria-describedby')
  userAriaDescribedBy!: string

  control: FormControl = new FormControl()
  options: string[] = []
  optionsTrigger: Subject<string> = new Subject<string>()
  private _value: string | null = null
  stateChanges: Subject<void> = new Subject<void>()
  id: string = `object-selector-${EnumSelectorComponent.nextId++}`
  private _placeholder: string = ''
  focused: boolean = false
  touched: boolean = false
  private _required: boolean = false
  private _disabled: boolean = false
  controlType: string = 'object-selector'
  onChange: (value: any) => void = () => {}
  onTouched: () => void = () => {}

  constructor(
    private http: HttpClient,
    @Optional()
    @Self()
    public ngControl: NgControl
  ) {
    if (this.ngControl != null) {
      this.ngControl.valueAccessor = this
    }
    this.control.valueChanges
      .subscribe(value => this.optionsTrigger.next(value))
    this.optionsTrigger
      .pipe(
        startWith(''),
        debounceTime(400),
        switchMap(filter => this.loadByFilter(filter))
      )
      .subscribe(result => this.options = result)
  }

  get value(): string | null {
    return this._value
  }
  set value(value: string | null) {
    if (!value) {
      this._value = null
    } else {
      this._value = value
    }
    this.stateChanges.next()
  }

  @Input()
  get placeholder(): string {
    return this._placeholder
  }
  set placeholder(placeholder: string) {
    this._placeholder = placeholder
  }

  get empty(): boolean {
    return this._value == null
  }

  @HostBinding('class.floating')
  get shouldLabelFloat() {
    return this.focused || !this.empty || this.control.value
  }

  @Input()
  get required(): boolean {
    return this._required
  }
  set required(required: boolean) {
    this._required = coerceBooleanProperty(required)
    this.stateChanges.next()
  }

  @Input()
  get disabled(): boolean {
    return this._disabled
  }
  set disabled(disabled: boolean) {
    this._disabled = coerceBooleanProperty(disabled)
    if (this._disabled) {
      this.control.disable()
    } else {
      this.control.enable()
    }
    this.stateChanges.next()
  }

  get errorState(): boolean {
    return this.control.value && this.value == null && !this.focused
  }

  ngOnDestroy(): void {
    this.stateChanges.complete()
  }

  onContainerClick(event: MouseEvent): void {
    this.optionsTrigger.next('')
  }

  setDescribedByIds(ids: string[]): void {}

  registerOnChange(fn: any): void {
    this.onChange = fn
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn
  }

  setDisabledState(disabled: boolean): void {
    this.disabled = disabled
  }

  writeValue(obj: string | null): void {
    this.value = obj
    if (obj) {
      let reference = this.options.find(option => option == obj);
      this.control.setValue(reference || '')
    } else {
      this.control.setValue('')
    }
  }

  onFocusIn() {
    if (!this.focused) {
      this.focused = true
      this.stateChanges.next()
    }
  }

  onFocusOut() {
    if (this.focused) {
      this.focused = false
      this.touched = false
      this.onTouched()
      this.stateChanges.next()
    }
  }

  private loadByFilter(filter: string): Observable<string[]> {
    return this.http.get<string[]>(environment.api + this.api + '?filter=' + filter)
  }

  updateValue(value: string) {
    this.value = value
    this.control.setValue(value)
    this.onChange(value)
  }

}
