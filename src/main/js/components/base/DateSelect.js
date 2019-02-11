import React from 'react';
import classNames from 'classnames';
import {range} from 'range';

const CONFIG_MIN_YEAR = 2010;
const CONFIG_MAX_YEAR = 2070;
const YEARS = range(CONFIG_MIN_YEAR, CONFIG_MAX_YEAR + 1);
const MONTHS = range(12);

const partialSelectStyles = {
    border: 'none',
    display: 'inline',
    backgroundColor: 'transparent',
    boxShadow: 'none',
    margin: 0,
    padding: 0
};

function zeroPad(number, length) {
    let str = '' + number;
    while (str.length < length) {
        str = '0' + str;
    }
    return str;
}

function daysInMonth(month, year) {
    return new Date(year, month + 1, 0).getDate();
}

export default class DateSelect extends React.Component {
    constructor(props, context) {
        super(props, context);

        this.initialYear = props.defaultYear || YEARS[0];
        this.initialMonth = props.defaultMonth || MONTHS[0];
        this.initialDate = props.defaultDate || 1;

        this.state = {
            year: this.initialYear,
            month: this.initialMonth,
            date: this.initialDate,
            hasFocus: false
        };
    }

    _handleDateChange(newTime) {
        this.setState(newTime, () => {
            if (this.props.onChange) {
                this.props.onChange({ year: this.state.year, month: this.state.month, date: this.state.date });
            }
        });
    }

    render() {
        const dayRange = range(1, daysInMonth(this.state.month, this.state.year) + 1);

        return (
            <span className={classNames({ 'select': true, 'focus': this.state.hasFocus })} onFocus={() => this.setState({ hasFocus: true })} onBlur={() => this.setState({ hasFocus: false })}>
                <select defaultValue={this.initialDate} style={partialSelectStyles} onChange={evt => this._handleDateChange({
                    date: parseInt(evt.target.value)
                })}>
                    {dayRange.map(day => (
                        <option key={`day-${day}`} value={day}>{zeroPad(day, 2)}</option>
                    ))}
                </select>
                {'.'}
                <select defaultValue={this.initialMonth} style={partialSelectStyles} onChange={evt => this._handleDateChange({
                    month: parseInt(evt.target.value)
                })}>
                    {MONTHS.map(month => (
                        <option key={`month-${month}`} value={month}>{zeroPad(month + 1, 2)}</option>
                    ))}
                </select>
                {'.'}
                <select defaultValue={this.initialYear} style={partialSelectStyles} onChange={evt => this._handleDateChange({
                    year: parseInt(evt.target.value)
                })}>
                    {YEARS.map(year => (
                        <option key={`year-${year}`} value={year}>{zeroPad(year, 4)}</option>
                    ))}
                </select>
            </span>
        );
    }
}
