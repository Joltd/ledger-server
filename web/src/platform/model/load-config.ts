export class LoadConfig {
  filter: FilterConfig = new FilterConfig()
  page: PageConfig = new PageConfig()
  sort: SortConfig = new SortConfig()
}

export class FilterConfig {
  expression!: FilterExpression
}

export class FilterExpression {
  reference!: string
  operator!: string
  value!: string
  type!: string
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
