'use strict';

/**
 * Parse the page parameters from the URL search component.
 */
const parseParameters = () => {
  const searchParams = window.location.search;

  if (!searchParams) {
    return null;
  }
  else {
    const pairs = searchParams.slice(1).split(/&/);
    const resultObj = {};
    for (const pair of pairs) {
      const [key, value] = pair.split(/=/, 2);
      resultObj[key] = value;
    }
    return resultObj;
  }
}

const reloadWithLastStoredParameters = () => {
  const lastParameters = JSON.parse(window.localStorage.getItem("user_list_last_parameters"));
  if (lastParameters !== null) {
    window.location.href = `?page=1&sortBy=${lastParameters.sortBy}&asc=${lastParameters.asc}`;
  }
};

(() => {
  // Executed every time the page loads.
  const params = parseParameters();
  if (params === null) {
    // The page has no parameters, default to the ones that we has last
    reloadWithLastStoredParameters();
  }
  else {
    // Page has parameters, save them.
    window.localStorage.setItem("user_list_last_parameters", JSON.stringify(params));
  }
})();