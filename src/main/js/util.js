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


function getMonthsSinceYear1AD(date) {
    if (!date) {
        return undefined;
    }
    return date.getFullYear() * 12 + date.getMonth();
}

function getMonthOffset(date) {
    if (!date) {
        return undefined;
    }
    return getMonthsSinceYear1AD(date) - getMonthsSinceYear1AD(new Date());
}

function isMonthOffsetWithinRange(offset, startDate, endDate) {
    return getMonthOffset(startDate) <= offset && offset <= getMonthOffset(endDate);
}

export function getWeek(date) {
    if (!date) {
        return undefined;
    }

    // https://weeknumber.com/how-to/javascript
    const tmp = new Date(date.getTime());
    tmp.setHours(0, 0, 0, 0);
    // Thursday in current week decides the year.
    tmp.setDate(tmp.getDate() + 3 - (tmp.getDay() + 6) % 7);
    // January 4 is always in week 1.
    const week1 = new Date(tmp.getFullYear(), 0, 4);
    // Adjust to Thursday in week 1 and count number of weeks from date to week1.
    return 1 + Math.round(((tmp.getTime() - week1.getTime()) / 86400000
        - 3 + (week1.getDay() + 6) % 7) / 7);
}

export function getClosestProjectMonth(monthOffset, startDate, endDate) {
    const validOffset = Math.max(getMonthOffset(startDate), Math.min(getMonthOffset(endDate), monthOffset));


    let date = new Date();
    date = new Date(date.getFullYear(), date.getMonth(), 1);
    date.setMonth(date.getMonth() + validOffset);
    const month = date.getMonth();

    //necessary for december -> january 
    let continiousMonth = getMonthsSinceYear1AD(date);
    var result = {
        monthOffset: validOffset,
        month,
        monthName: date.toLocaleString('default', { month: 'long' }),
        days: [],
        firstValidDate: null,
        lastValidDate: null,
        isNextOffsetValid: isMonthOffsetWithinRange(validOffset + 1, startDate, endDate),
        isPreviousOffsetValid: isMonthOffsetWithinRange(validOffset - 1, startDate, endDate),
    };

    // show full week in calendar
    // negative offset for Weekdays 0: -6; 1: 0;  2: -1;  3: -2;  4: -3;  5: -4;  6: -5;
    var offset = ((date.getDay() + 6) % 7);
    date.setDate(date.getDate() - offset);

    while (getMonthsSinceYear1AD(date) <= continiousMonth || date.getDay() != 1) {
        const disabled = date.getMonth() !== month || startDate > date || date > endDate;
        if (!disabled) {
            if (!result.firstValidDate) {
                result.firstValidDate = new Date(date);
            }
            result.lastValidDate = new Date(date);
        }
        const day = {
            date: new Date(date.getTime()),
            disabled,
        };
        result.days.push(day);
        date.setDate(date.getDate() + 1);
    }
    return result;
}

/** -> DD.MM. */
export function convertToDDMM(date) {
    if (!date) {
        return undefined;
    }
    return _.padStart(date.getDate(), 2, '0') + "." + _.padStart(date.getMonth() + 1, 2, '0') + ".";
}
/** -> DD.MM.YYYY */
export function convertToDDMMYYYY(date) {
    if (!date) {
        return undefined;
    }
    return convertToDDMM(date) + date.getFullYear();
}
/** -> DD.MM.YYYY HH:MM */
export function convertToDDMMYYYY_HHMM(date) {
    if (!date) {
        return undefined;
    }
    return convertToDDMMYYYY(date) + " " + _.padStart(date.getHours() + 1, 2, '0') + ":" + _.padStart(date.getMinutes() + 1, 2, '0');
}

/** -> YYYY-MM-DD */
export function convertToYYYYMMDD(date) {
    if (!date) {
        return undefined;
    }
    return date.getFullYear() + "-" + _.padStart(date.getMonth() + 1, 2, '0') + "-" + _.padStart(date.getDate(), 2, '0');
}

/** returns if the dates represent the same day (ignores time) */
export function dateEquals(date1, date2) {
    return date1.getFullYear() === date2.getFullYear() && date1.getMonth() === date2.getMonth() && date1.getDate() === date2.getDate();
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

export function getIdMapValues(idMap) {
    return idMap && [...idMap.values()];
}

export function encodeNumber(number) {
    let rest;
    let divisable = number;
    let encoded = '';
    while (divisable > 0) {
        rest = divisable % 62;
        if (rest > 35) {
            // small character: a=97, z=122
            encoded += String.fromCharCode(rest + 61);
        } else if (rest > 9) {
            // big character: A=65, Z=90
            encoded += String.fromCharCode(rest + 55);
        } else {
            // number: 0=48, 9=57
            encoded += String.fromCharCode(rest + 48);
        }
        divisable = (divisable - rest) / 62;
    }
    return encoded;
}

export function cyrb53(str, seed = 0) {
    let h1 = 0xdeadbeef ^ seed, h2 = 0x41c6ce57 ^ seed;
    for (let i = 0, ch; i < str.length; i++) {
        ch = str.charCodeAt(i);
        h1 = Math.imul(h1 ^ ch, 2654435761);
        h2 = Math.imul(h2 ^ ch, 1597334677);
    }
    h1 = Math.imul(h1 ^ h1 >>> 16, 2246822507) ^ Math.imul(h2 ^ h2 >>> 13, 3266489909);
    h2 = Math.imul(h2 ^ h2 >>> 16, 2246822507) ^ Math.imul(h1 ^ h1 >>> 13, 3266489909);
    return 4294967296 * (2097151 & h2) + (h1 >>> 0);
};

export function generateUniqueId() {
    return encodeNumber(cyrb53(Date.now() + '' + window.performance.now()));
}

//https://stackoverflow.com/a/16245768/6527256
export function base64toBlob(base64Data, contentType = '', sliceSize = 512) {
    const byteCharacters = atob(base64Data);
    const byteArrays = [];

    for (let offset = 0; offset < byteCharacters.length; offset += sliceSize) {
        const slice = byteCharacters.slice(offset, offset + sliceSize);

        const byteNumbers = new Array(slice.length);
        for (let i = 0; i < slice.length; i++) {
            byteNumbers[i] = slice.charCodeAt(i);
        }

        const byteArray = new Uint8Array(byteNumbers);
        byteArrays.push(byteArray);
    }

    const blob = new Blob(byteArrays, { type: contentType });
    return blob;
}

export function wrapSetStateInPromise(context, setterFunction) {
    return new Promise((resolve, reject) => {
        context.setState(prevState => setterFunction(prevState),
            () => resolve());
    });
}

export function getQueryParams() {
    const search = window.location.search;
    const params = new Map();
    if (search.length > 1) {
        search.slice(search.indexOf('?') + 1).split('&').forEach(keyValuePair => {
            const parts = keyValuePair.split('=');
            params.set(parts[0], parts.length > 1 ? decodeURI(parts[1]) : '');
        });
    }
    return params;
}


// items don't have any prefix, because it's possible to use the existing barcodes on the items
export function getItemBarcodeString(itemIdentifier) {
    return itemIdentifier;
}

export function getSlotBarcodeString(storeId, slotName) {
    return storeId + ' ' + slotName;
}

// FUTURE: getUserBarcodeString considering existing IDs from Builder Assistent, probably it's not possible to detect if the barcode represents an item or user bc we can't influence the format
