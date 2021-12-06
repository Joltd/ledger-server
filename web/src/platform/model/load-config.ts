export class LoadConfig {
  filter: FilterConfig = new FilterConfig()
  page: PageConfig = new PageConfig()
  sort: SortConfig = new SortConfig()
}

export class FilterConfig {
  expression?: FilterExpression
}

export class FilterExpression {
  reference!: string
  operator!: OperatorType
  value!: string
  type!: FilterExpressionType
  expressions: FilterExpression[] = []
}

export class PageConfig {
  index: number = 0
  size: number = 50
  length: number = 0
}

export class SortConfig {
  reference?: string
  order: 'ASC' | 'DESC' = 'ASC'
}

export enum FilterExpressionType {
  OR = 'OR',
  AND = 'AND',
  NOT = 'NOT',
  STATEMENT = 'STATEMENT'
}

export enum OperatorType {
  EQUAL = 'EQUAL',
  NOT_EQUAL = 'NOT_EQUAL',
  LESS = 'LESS',
  LESS_EQUAL = 'LESS_EQUAL',
  GREATER = 'GREATER',
  GREATER_EQUAL = 'GREATER_EQUAL',
  LIKE = 'LIKE',
  NOT_LIKE = 'NOT_LIKE',
  IS_NULL = 'IS_NULL',
  IS_NOT_NULL = 'IS_NOT_NULL'
}
