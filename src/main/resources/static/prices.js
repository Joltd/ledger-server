for (let entry of prices.entries) {
    let element = document.getElementById(entry.ticker);
    if (!element) {
        console.log('Unable to found chart ' + entry.ticker);
        continue;
    }
    echarts.init(element).setOption(makeOptions(entry));
}

function makeOptions(entry) {
    return {
        textStyle: {
            fontSize: 18
        },
        title: {
            text: entry.ticker
        },
        dataZoom: {
            start: 60
        },
        tooltip: {
            trigger: 'axis',
            textStyle: {
                fontSize: 18
            }
        },
        xAxis: {
            data: prices.dates,
            axisLabel: {
                fontSize: 18
            }
        },
        yAxis: {
            max: function (value) {
                return value.max + offset(value);
            },
            min: function (value) {
                return value.min - offset(value);
            }
        },
        series: [
            {
                type: 'line',
                name: entry.ticker,
                symbol: 'none',
                data: entry.prices
            }
        ]
    };
}

function offset(value) {
    return (value.max - value.min) * 0.1
}
