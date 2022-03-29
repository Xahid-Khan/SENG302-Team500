'use strict';

const reloadWithLastStoredParameters = () => {
  const lastParameters = window.localStorage.getItem("user_list_last_parameters");
  if (lastParameters !== null) {
    window.location.href = lastParameters;
  }
};

(() => {
  // Executed every time the page loads.
  const searchParams = window.location.search;
  if (!searchParams) {
    // The page has no parameters, default to the ones that we has last
    reloadWithLastStoredParameters();
  }
  else {
    // Page has parameters, save them.
    window.localStorage.setItem("user_list_last_parameters", searchParams);
  }
})();