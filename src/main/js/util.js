import moment from 'moment';
import React from 'react';
import { Redirect } from 'react-router';
import { fullPathOfLogin } from './paths';
import { SessionContext } from './providers/session-provider';

export function wrapComponent(Component, additionalProps) {
    return props => (<Component {...props} {...additionalProps} />);
}

// don't use if state in context provider changes often
// bc it causes a complete remount of all subcomponents
// TODO fix this behavior
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
                ? <Component {...props} sessionState={sessionState} />
                : <Redirect to={fullPathOfLogin()} />
            }
        </SessionContext.Consumer>
    );
}

export function isStringBlank(str) {
    if (str) {
        const trimmed = str.trim();
        return !trimmed || trimmed.length === 0;
    }
    return true;
}

export function isAnyStringBlank(strings) {
    return strings.some(isStringBlank);
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
    return !moment().utc().add(offset, 'months').endOf('month').isBefore(startDate) && !moment().utc().add(offset, 'months').startOf('month').isAfter(endDate);
}


function getMonthsSinceYear1AD(date) {
    return date.year() * 12 + date.month();
}

export function getMonthOffsetWithinRange(originalOffset, startDate, endDate) {
    if (!isMonthOffsetWithinRange(originalOffset, startDate, endDate)) {
        // it seems like moment.diff(...,'months') calculates the difference of full months
        return getMonthsSinceYear1AD(startDate) - getMonthsSinceYear1AD(moment().utc());
    }
    return originalOffset;
}

export function getMonthNameForOffset(offset) {
    return ['Januar', 'Februar', 'März', 'April', 'Mai', 'Juni', 'Juli', 'August', 'September', 'Oktober', 'November', 'Dezember'][moment().utc().add(offset, 'months').month()];
}


export function getClosestProjectMonth(monthOffset, startDate, endDate) {
    const validOffset = getMonthOffsetWithinRange(monthOffset, startDate, endDate);

    var date = moment().utc().startOf('month').add(validOffset, 'months');
    let month = date.clone().month();
    //necessary for december -> january 
    let continiousMonth = getMonthsSinceYear1AD(date.clone());
    let endOfMonth = date.clone().endOf('month');
    var result = {
        monthOffset: validOffset,
        month,
        monthName: getMonthNameForOffset(validOffset),
        days: [],
        firstValidDate: null,
        lastValidDate: null,
        isNextOffsetValid: isMonthOffsetWithinRange(validOffset + 1, startDate, endDate),
        isPreviousOffsetValid: isMonthOffsetWithinRange(validOffset - 1, startDate, endDate),
    };

    // offset for Weekdays 1: 0;  2: -1;  3: -2;  4: -3;  5: -4;  6: -5;  7: -6
    var offset = date.isoWeekday() * (-1) + 1;
    date.add(offset, 'days');

    while (getMonthsSinceYear1AD(date) <= continiousMonth || (date.isoWeekday() > 1 && date.isoWeekday() <= 7)) {
        const disabled = date.month() !== month || date.isBefore(startDate) || date.isAfter(endDate);
        if (!disabled) {
            if (!result.firstValidDate) {
                result.firstValidDate = date.clone();
            }
            result.lastValidDate = date.clone();
        }
        const day = {
            date: date.clone(),
            disabled,
        };
        result.days.push(day);
        date = date.add(1, 'days');
    }
    return result;
}

const READABLE_DATE_FORMAT = 'DD.MM.YYYY';
export function convertToReadableFormat(moment) {
    return moment.format(READABLE_DATE_FORMAT);
}
const READABLE_DATE_FORMAT_WITHOUT_YEAR = 'DD.MM.';
export function convertToReadableFormatWithoutYear(moment) {
    return moment.format(READABLE_DATE_FORMAT_WITHOUT_YEAR);
}
const READABLE_DATE_FORMAT_WITH_TIME = 'DD.MM.YYYY HH:mm';
export function convertToReadableFormatWithTime(moment) {
    return moment.format(READABLE_DATE_FORMAT_WITH_TIME);
}

const MUI_DATE_FORMAT = 'YYYY-MM-DD';
export function convertToMUIFormat(moment) {
    return moment && moment.format(MUI_DATE_FORMAT);
}
export function convertFromMUIFormat(date) {
    return !isStringBlank(date) && moment(date, MUI_DATE_FORMAT);
}

const ROLE_NAMES = new Map();
ROLE_NAMES.set('ROLE_STORE_KEEPER', 'Magaziner');
ROLE_NAMES.set('ROLE_INVENTORY_MANAGER', 'Lagerist');
ROLE_NAMES.set('ROLE_ATTENDANCE', 'Anwesenheit');
ROLE_NAMES.set('ROLE_CONSTRUCTION_SERVANT', 'Baudiener');
ROLE_NAMES.set('ROLE_LOCAL_COORDINATOR', 'Helferkoordinator');
ROLE_NAMES.set('ROLE_ADMIN', 'Administrator');
ROLE_NAMES.set('ROLE_PUBLISHER', 'Verkündiger');
export function getRoleName(role) {
    return ROLE_NAMES.get(role);
}

export function convertToIdMap(idObjectList) {
    const idMap = new Map();
    idObjectList.forEach(idObject => {
        idMap.set(idObject.id, idObject);
    });
    return idMap;
}