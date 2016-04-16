import React from 'react';
import TestUtils from 'react-addons-test-utils';
import { LoginView } from 'views/LoginView';

function shallowRender (component) {
  const renderer = TestUtils.createRenderer();

  renderer.render(component);
  return renderer.getRenderOutput();
}

function renderWithProps (props = {}) {
  return TestUtils.renderIntoDocument(<LoginView {...props} />);
}

function shallowRenderWithProps (props = {}) {
  return shallowRender(<LoginView {...props} />);
}

describe('(View) Login', function () {
  let _component, _rendered, _props, _spies;

  beforeEach(function () {
    _spies = {dispatch: sinon.spy()};
    _props = {
      session: {triedToAuthenticate: true, isAuthenticated: false},
      location: {},
      params: {},
      dispatch: _spies.dispatch
    };

    _component = shallowRenderWithProps(_props);
    _rendered = renderWithProps(_props);
  });

  it('Should render as a <div>.', function () {
    expect(_component.type).to.equal('div');
  });

  it('Should include an <h1> with welcome text.', function () {
    const h1 = TestUtils.findRenderedDOMComponentWithTag(_rendered, 'h1');

    expect(h1).to.exist;
    expect(h1.textContent).to.match(/donatr/);
  });

  it('Should render with an <h3> that includes the view name.', function () {
    const h2 = TestUtils.findRenderedDOMComponentWithTag(_rendered, 'h3');

    expect(h2).to.exist;
    expect(h2.textContent).to.match(/login/);
  });

  describe('An login form', function () {
    let _form;

    beforeEach(() => {
      _form = TestUtils.scryRenderedDOMComponentsWithTag(_rendered, 'form')
        .filter(a => /\/login/.test(a.action))[0];
    });

    it('should be rendered.', function () {
      expect(_form).to.exist;
    });

    it('should dispatch an action when submitted.', function () {
      _spies.dispatch.should.have.not.been.called;
      TestUtils.Simulate.submit(_form);
      _spies.dispatch.should.have.been.called;
    });
  });

  describe('An login button...', function () {
    let _btn;

    beforeEach(() => {
      _btn = TestUtils.scryRenderedDOMComponentsWithTag(_rendered, 'button')
        .filter(a => /Login/.test(a.textContent))[0];
    });

    it('should be rendered as submit.', function () {
      expect(_btn).to.exist;
      expect(_btn.type).to.match(/submit/);
    });
  });
});
