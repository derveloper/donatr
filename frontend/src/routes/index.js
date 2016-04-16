import React from 'react';
import { Route, IndexRoute } from 'react-router';
import CoreLayout from 'layouts/CoreLayout/CoreLayout';
import AccountsView from 'views/AccountsView';
import LoginView from 'views/LoginView';
import DonateView from 'views/DonateView';
import CreditView from 'views/CreditView';
import {requireAuthentication} from 'components/AuthenticatedComponent';

export default (
  <Route path='/' component={CoreLayout}>
    <IndexRoute component={requireAuthentication(AccountsView)}/>
    <Route path='/login' component={LoginView} />
    <Route path='/donate' component={requireAuthentication(DonateView)}/>
    <Route path='/credit' component={requireAuthentication(CreditView)}/>
  </Route>
);
