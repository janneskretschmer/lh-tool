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
                ? <Component {...props} />
                : <Redirect to={fullPathOfLogin()} />
            }
        </SessionContext.Consumer>
    );
}

export function setWaitingState(waiting) {
  document.body.style.cursor = waiting ? 'wait' : 'default';
}

export function getMonthArrayWithOffsets(start, end) {
  const today = moment().startOf('day');
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
