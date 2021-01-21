echarts.init(document.getElementById('portfolio'))
    .setOption({
        textStyle: {
            fontSize: 18
        },
        series: [
            {
                type: 'pie',
                clockwise: false,
                data: entries.map(entry => {
                    return {
                        name: entry.ticker, value: entry.balance
                    }
                }).sort((left, right) => left.value - right.value),
                label: {
                    formatter: '{b}, {d}%'
                }
            }
        ]
    });

echarts.init(document.getElementById('profitability'))
    .setOption({
        textStyle: {
            fontSize: 18
        },
        xAxis: {
            type: 'category',
            data: entries.sort((left, right) => right.profitability - left.profitability).map(entry => entry.ticker),
            axisLabel: {
                fontSize: 18
            }
        },
        yAxis: {
            axisLabel: {
                formatter: '{value}%',
                fontSize: 18
            }
        },
        series: [
            {
                type: 'bar',
                data: entries.map(entry => {
                    return {
                        name: entry.ticker, value: entry.profitability
                    }
                }).sort((left, right) => right.value - left.value),
                label: {
                    show: true,
                    position: 'top',
                    formatter: p => p.value.toFixed(2) + '%'
                }
            }
        ]
    });

echarts.init(document.getElementById('income'))
    .setOption({
        textStyle: {
            fontSize: 18
        },
        xAxis: {
            type: 'category',
            data: entries.sort((left, right) => right.income - left.income).map(entry => entry.ticker),
            axisLabel: {
                fontSize: 18
            }
        },
        yAxis: {
            axisLabel: {
                fontSize: 18
            }
        },
        series: [
            {
                type: 'bar',
                data: entries.map(entry => {
                    return {
                        name: entry.ticker, value: entry.income
                    }
                }).sort((left, right) => right.value - left.value),
                label: {
                    show: true,
                    position: 'top',
                    formatter: p => Math.trunc(p.value)
                }
            }
        ]
    });

function toggleCharts() {
    let percentage = document.getElementById('percentage');
    let values = document.getElementById('values');

    if (percentage.classList.contains('d-none')) {
        percentage.classList.remove('d-none');
        values.classList.add('d-none');
    } else {
        percentage.classList.add('d-none');
        values.classList.remove('d-none');
    }
}