import { Injectable } from '@angular/core';
import {
  HTTP_INTERCEPTORS,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
  HttpResponse
} from "@angular/common/http";
import {Observable, throwError} from "rxjs";
import {catchError, map} from "rxjs/operators";
import {ErrorService} from "./error.service";
import {plainToClass} from "class-transformer";
import {Descriptor} from "../../platform/model/descriptor";
import {TypeUtils} from "../../core/type-utils";

@Injectable({
  providedIn: 'root'
})
export class RestInterceptorService implements HttpInterceptor {

  constructor(private errorService: ErrorService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (req.responseType != 'json') {
      return next.handle(req);
    }

    return next.handle(req)
      .pipe(
        map(event => {

          if (!(event instanceof HttpResponse)) {
            return event
          }

          if (!event.ok) {
            this.errorService.transportError(event.status)
            return event
          }

          let body = event.body as ResponseBody
          if (!body.body && !body.error) {
            return event
          }

          if (!body.success) {
            this.errorService.logicError(body.error)
            throw new Error(body.error);
          }

          let type = TypeUtils.get(req)
          if (!type) {
            return event
          }

          return event.clone({
            body: plainToClass(type, body.body)
          })

        }),
        catchError(error => throwError(error))
      )
  }

}

export const restInterceptorProvider = [
  { provide: HTTP_INTERCEPTORS, useClass: RestInterceptorService, multi: true }
]

class ResponseBody {
  success: boolean = false
  body!: object
  error!: string
}
