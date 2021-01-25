echarts.init(document.getElementById('prices'))
    .setOption({
        textStyle: {
            fontSize: 18
        },
        dataZoom: {

        },
        tooltip: {
            trigger: 'axis',
            textStyle: {
                fontSize: 18
            }
        },
        legend: {
            data: prices.entries.map(entry => entry.ticker)
        },
        xAxis: {
            data: prices.dates,
            axisLabel: {
                fontSize: 18
            }
        },
        yAxis: prices.entries.map((entry, index) => {
            return {
                show: false,
                max: function (value) {
                    return value.max * 1.1
                },
                min: function (value) {
                    return value.min * 0.7
                },
            };
        }),
        series: prices.entries.map((entry, index) => {
            return {
                type: 'line',
                name: entry.ticker,
                yAxisIndex: index,
                symbol: 'none',
                data: entry.prices
            };
        })
    });