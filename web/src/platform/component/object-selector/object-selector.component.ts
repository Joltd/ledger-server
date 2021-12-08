import {Component, ElementRef, HostBinding, Input, OnDestroy, Optional, Self, ViewChild} from '@angular/core';
import {ControlValueAccessor, FormControl, NgControl} from "@angular/forms";
import {Observable, Subject} from "rxjs";
import {Reference} from "../../model/entity";
import {debounceTime, distinctUntilChanged, startWith, switchMap} from "rxjs/operators";
import {HttpClient} from "@angular/common/http";
import {TypeUtils} from "../../../core/type-utils";
import {MatFormFieldControl} from "@angular/material/form-field";
import {coerceBooleanProperty} from "@angular/cdk/coercion";

@Component({
  selector: 'object-selector',
  templateUrl: './object-selector.component.html',
  styleUrls: ['./object-selector.component.scss'],
  providers: [{provide: MatFormFieldControl, useExisting: ObjectSelectorComponent}],
  host: {
    '[class.example-floating]': 'shouldLabelFloat',
    '[id]': 'id',
  }
})
export class ObjectSelectorComponent implements MatFormFieldControl<Reference>,ControlValueAccessor,OnDestroy {

  private static nextId: number = 0

  @Input()
  api!: string
  @ViewChild('input')
  input!: ElementRef
  @Input('aria-describedby')
  userAriaDescribedBy!: string

  control: FormControl = new FormControl()
  options!: Observable<Reference[]>
  private _value: Reference | null = null
  stateChanges: Subject<void> = new Subject<void>()
  id: string = `object-selector-${ObjectSelectorComponent.nextId++}`
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
    this.options = this.control.valueChanges
      .pipe(
        startWith(''),
        debounceTime(400),
        distinctUntilChanged(),
        switchMap(filter => this.loadByFilter(filter))
      )
  }

  get value(): Reference | null {
    return this._value
  }
  set value(reference: Reference | null) {
    if (!reference) {
      this._value = null
    } else {
      this._value = reference
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
    this.input.nativeElement.focus()
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

  writeValue(obj: Reference | null): void {
    this.value = obj
    this.control.setValue(obj?.name)
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

  private loadByFilter(filter: string): Observable<Reference[]> {
    return this.http.get<Reference[]>(this.api + '?filter=' + filter, TypeUtils.of(Reference))
  }

  updateValue(value: Reference) {
    this.value = value
    this.control.setValue(this.value.name)
    this.onChange(value)
  }

}
