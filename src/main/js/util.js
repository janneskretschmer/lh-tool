import React from 'react';
import { Redirect } from 'react-router'
import { SessionContext } from './providers/session-provider';
import { fullPathOfLogin } from './paths';
import moment from 'moment';

export function wrapComponent(Component, additionalProps) {
    return props => (<Component {...props} {...additionalProps} />);
}

export function withContext(propName, Context) {
    return function withContextDecorator(Component) {
        return props => (
            <Context.Consumer>
                {contextState => {
                    const WrappedContext = wrapComponent(Component, { [propName]: contextState });
                    return (<WrappedContext {...props}>{props.children}</WrappedContext>);
                }}
            </Context.Consumer>
        );
    };
}

export function requiresLogin(Component) {
    // FUTURE: As soon as isomorphic rendering is employed the redirect needs to be bubbled up
    return props => (
        <SessionContext.Consumer>
            {sessionState => sessionState.isLoggedIn()
                ? <Component {...props} sessionState={sessionState}/>
                : <Redirect to={fullPathOfLogin()} />
            }
        </SessionContext.Consumer>
    );
}

export function setWaitingState(waiting) {
  document.body.style.cursor = waiting ? 'wait' : 'default';
}

export function getMonthArrayWithOffsets(start, end) {
  const today = moment().utc().startOf('day');
  let date = start.diff(today, 'days') > 0 ? start.clone() : today.clone();
  let months = [];
  while (end.diff(date, 'days') > 0) {
    const monthEnd = date.clone().endOf('month');
    months = [
      ...months,
      {
        month: date.format('M'),
        startOffset: date.diff(today, 'days'),
        endOffset: monthEnd.diff(today, 'days'),
      }];
    date = monthEnd.add(1, 'days');
  }
  return months;
}

export function isMonthOffsetWithinRange(offset, startDate, endDate) {
    return !moment().utc().add(offset,'months').endOf('month').isBefore(startDate) && !moment().utc().add(offset,'months').startOf('month').isAfter(endDate)
}

export function getMonthOffsetWithinRange(originalOffset, startDate, endDate){
    if(!isMonthOffsetWithinRange(originalOffset, startDate, endDate)){
        // it seems like moment.diff(...,'months') calculates the difference of full months
        return getMonthsSinceYear1AD(startDate) - getMonthsSinceYear1AD(moment().utc());
    }
    return originalOffset;
}

export function getMonthNameForOffset(offset) {
    return ['Januar','Februar','Maerz','April','Mai','Juni','Juli','August','September','Oktober','November','Dezember'][moment().utc().add(offset,'months').month()]
}


export function getProjectMonth(monthOffset, startDate, endDate) {
    var date = moment().utc().startOf('month').add(monthOffset,'months');
    let month = date.clone().month();
    //necessary for december -> january 
    let continiousMonth = getMonthsSinceYear1AD(date.clone())
    let endOfMonth = date.clone().endOf('month');
    var result = {
        monthOffset,
        month,
        monthName: getMonthNameForOffset(monthOffset),
        startDiff: (date.isAfter(startDate) ? date.clone() : startDate).diff(moment().utc().startOf('day'), 'days'),
        endDiff:  (endOfMonth.isBefore(endDate) ? endOfMonth : endDate).diff(moment().utc().startOf('day'), 'days'),
        days:[],
    };

    // offset for Weekdays 1: 1;  2: 0;  3: -1;  4: -2;  5: -3;  6: -4;  7: 2
    var offset = date.isoWeekday() * (-1) + 2;
    if (offset < -4) {
        offset  += 7;
    }
    date.add(offset, 'days');

    while(getMonthsSinceYear1AD(date) <= continiousMonth || (date.isoWeekday() > 2 && date.isoWeekday() < 7)) {
        if(date.isoWeekday() > 1 && date.isoWeekday() < 7){
            let day = {
                date: date.clone(),
                disabled: date.month() !== month || date.isBefore(startDate) || date.isAfter(endDate),
            }
            result.days.push(day);
        }
        date =  date.add(1, 'days')
    }
    return result;
}

function getMonthsSinceYear1AD(date){
    return date.year() * 12 + date.month()
}
