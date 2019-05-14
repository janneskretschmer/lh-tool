<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8"/>
<meta name="viewport" content="minimum-scale=1, initial-scale=1, width=device-width, shrink-to-fit=no">
<title>LH-Tool</title>
<!-- Roboto fonts -->
<style>
/* roboto-300 - cyrillic_latin_latin-ext_greek_cyrillic-ext_greek-ext */
@font-face {
  font-family: 'Roboto';
  font-style: normal;
  font-weight: 300;
  src: url('${contextPath}/static/fonts/roboto/roboto-v18-cyrillic_latin_latin-ext_greek_cyrillic-ext_greek-ext-300.eot'); /* IE9 Compat Modes */
  src:
    local('Roboto Light'),
    local('Roboto-Light'),
    url('${contextPath}/static/fonts/roboto/roboto-v18-cyrillic_latin_latin-ext_greek_cyrillic-ext_greek-ext-300.eot?#iefix') format('embedded-opentype'), /* IE6-IE8 */
    url('${contextPath}/static/fonts/roboto/roboto-v18-cyrillic_latin_latin-ext_greek_cyrillic-ext_greek-ext-300.woff2') format('woff2'), /* Super Modern Browsers */
    url('${contextPath}/static/fonts/roboto/roboto-v18-cyrillic_latin_latin-ext_greek_cyrillic-ext_greek-ext-300.woff') format('woff'), /* Modern Browsers */
    url('${contextPath}/static/fonts/roboto/roboto-v18-cyrillic_latin_latin-ext_greek_cyrillic-ext_greek-ext-300.ttf') format('truetype'), /* Safari, Android, iOS */
    url('${contextPath}/static/fonts/roboto/roboto-v18-cyrillic_latin_latin-ext_greek_cyrillic-ext_greek-ext-300.svg#Roboto') format('svg'); /* Legacy iOS */
}

/* roboto-300italic - cyrillic_latin_latin-ext_greek_cyrillic-ext_greek-ext */
@font-face {
  font-family: 'Roboto';
  font-style: italic;
  font-weight: 300;
  src: url('${contextPath}/static/fonts/roboto/roboto-v18-cyrillic_latin_latin-ext_greek_cyrillic-ext_greek-ext-300italic.eot'); /* IE9 Compat Modes */
  src:
    local('Roboto Light Italic'),
    local('Roboto-LightItalic'),
    url('${contextPath}/static/fonts/roboto/roboto-v18-cyrillic_latin_latin-ext_greek_cyrillic-ext_greek-ext-300italic.eot?#iefix') format('embedded-opentype'), /* IE6-IE8 */
    url('${contextPath}/static/fonts/roboto/roboto-v18-cyrillic_latin_latin-ext_greek_cyrillic-ext_greek-ext-300italic.woff2') format('woff2'), /* Super Modern Browsers */
    url('${contextPath}/static/fonts/roboto/roboto-v18-cyrillic_latin_latin-ext_greek_cyrillic-ext_greek-ext-300italic.woff') format('woff'), /* Modern Browsers */
    url('${contextPath}/static/fonts/roboto/roboto-v18-cyrillic_latin_latin-ext_greek_cyrillic-ext_greek-ext-300italic.ttf') format('truetype'), /* Safari, Android, iOS */
    url('${contextPath}/static/fonts/roboto/roboto-v18-cyrillic_latin_latin-ext_greek_cyrillic-ext_greek-ext-300italic.svg#Roboto') format('svg'); /* Legacy iOS */
}

/* roboto-regular - cyrillic_latin_latin-ext_greek_cyrillic-ext_greek-ext */
@font-face {
  font-family: 'Roboto';
  font-style: normal;
  font-weight: 400;
  src: url('${contextPath}/static/fonts/roboto/roboto-v18-cyrillic_latin_latin-ext_greek_cyrillic-ext_greek-ext-regular.eot'); /* IE9 Compat Modes */
  src:
    local('Roboto'),
    local('Roboto-Regular'),
    url('${contextPath}/static/fonts/roboto/roboto-v18-cyrillic_latin_latin-ext_greek_cyrillic-ext_greek-ext-regular.eot?#iefix') format('embedded-opentype'), /* IE6-IE8 */
    url('${contextPath}/static/fonts/roboto/roboto-v18-cyrillic_latin_latin-ext_greek_cyrillic-ext_greek-ext-regular.woff2') format('woff2'), /* Super Modern Browsers */
    url('${contextPath}/static/fonts/roboto/roboto-v18-cyrillic_latin_latin-ext_greek_cyrillic-ext_greek-ext-regular.woff') format('woff'), /* Modern Browsers */
    url('${contextPath}/static/fonts/roboto/roboto-v18-cyrillic_latin_latin-ext_greek_cyrillic-ext_greek-ext-regular.ttf') format('truetype'), /* Safari, Android, iOS */
    url('${contextPath}/static/fonts/roboto/roboto-v18-cyrillic_latin_latin-ext_greek_cyrillic-ext_greek-ext-regular.svg#Roboto') format('svg'); /* Legacy iOS */
}

/* roboto-italic - cyrillic_latin_latin-ext_greek_cyrillic-ext_greek-ext */
@font-face {
  font-family: 'Roboto';
  font-style: italic;
  font-weight: 400;
  src: url('${contextPath}/static/fonts/roboto/roboto-v18-cyrillic_latin_latin-ext_greek_cyrillic-ext_greek-ext-italic.eot'); /* IE9 Compat Modes */
  src:
    local('Roboto Italic'),
    local('Roboto-Italic'),
    url('${contextPath}/static/fonts/roboto/roboto-v18-cyrillic_latin_latin-ext_greek_cyrillic-ext_greek-ext-italic.eot?#iefix') format('embedded-opentype'), /* IE6-IE8 */
    url('${contextPath}/static/fonts/roboto/roboto-v18-cyrillic_latin_latin-ext_greek_cyrillic-ext_greek-ext-italic.woff2') format('woff2'), /* Super Modern Browsers */
    url('${contextPath}/static/fonts/roboto/roboto-v18-cyrillic_latin_latin-ext_greek_cyrillic-ext_greek-ext-italic.woff') format('woff'), /* Modern Browsers */
    url('${contextPath}/static/fonts/roboto/roboto-v18-cyrillic_latin_latin-ext_greek_cyrillic-ext_greek-ext-italic.ttf') format('truetype'), /* Safari, Android, iOS */
    url('${contextPath}/static/fonts/roboto/roboto-v18-cyrillic_latin_latin-ext_greek_cyrillic-ext_greek-ext-italic.svg#Roboto') format('svg'); /* Legacy iOS */
}

/* roboto-500 - cyrillic_latin_latin-ext_greek_cyrillic-ext_greek-ext */
@font-face {
  font-family: 'Roboto';
  font-style: normal;
  font-weight: 500;
  src: url('${contextPath}/static/fonts/roboto/roboto-v18-cyrillic_latin_latin-ext_greek_cyrillic-ext_greek-ext-500.eot'); /* IE9 Compat Modes */
  src:
    local('Roboto Medium'),
    local('Roboto-Medium'),
    url('${contextPath}/static/fonts/roboto/roboto-v18-cyrillic_latin_latin-ext_greek_cyrillic-ext_greek-ext-500.eot?#iefix') format('embedded-opentype'), /* IE6-IE8 */
    url('${contextPath}/static/fonts/roboto/roboto-v18-cyrillic_latin_latin-ext_greek_cyrillic-ext_greek-ext-500.woff2') format('woff2'), /* Super Modern Browsers */
    url('${contextPath}/static/fonts/roboto/roboto-v18-cyrillic_latin_latin-ext_greek_cyrillic-ext_greek-ext-500.woff') format('woff'), /* Modern Browsers */
    url('${contextPath}/static/fonts/roboto/roboto-v18-cyrillic_latin_latin-ext_greek_cyrillic-ext_greek-ext-500.ttf') format('truetype'), /* Safari, Android, iOS */
    url('${contextPath}/static/fonts/roboto/roboto-v18-cyrillic_latin_latin-ext_greek_cyrillic-ext_greek-ext-500.svg#Roboto') format('svg'); /* Legacy iOS */
}

/* roboto-500italic - cyrillic_latin_latin-ext_greek_cyrillic-ext_greek-ext */
@font-face {
  font-family: 'Roboto';
  font-style: italic;
  font-weight: 500;
  src: url('${contextPath}/static/fonts/roboto/roboto-v18-cyrillic_latin_latin-ext_greek_cyrillic-ext_greek-ext-500italic.eot'); /* IE9 Compat Modes */
  src:
    local('Roboto Medium Italic'),
    local('Roboto-MediumItalic'),
    url('${contextPath}/static/fonts/roboto/roboto-v18-cyrillic_latin_latin-ext_greek_cyrillic-ext_greek-ext-500italic.eot?#iefix') format('embedded-opentype'), /* IE6-IE8 */
    url('${contextPath}/static/fonts/roboto/roboto-v18-cyrillic_latin_latin-ext_greek_cyrillic-ext_greek-ext-500italic.woff2') format('woff2'), /* Super Modern Browsers */
    url('${contextPath}/static/fonts/roboto/roboto-v18-cyrillic_latin_latin-ext_greek_cyrillic-ext_greek-ext-500italic.woff') format('woff'), /* Modern Browsers */
    url('${contextPath}/static/fonts/roboto/roboto-v18-cyrillic_latin_latin-ext_greek_cyrillic-ext_greek-ext-500italic.ttf') format('truetype'), /* Safari, Android, iOS */
    url('${contextPath}/static/fonts/roboto/roboto-v18-cyrillic_latin_latin-ext_greek_cyrillic-ext_greek-ext-500italic.svg#Roboto') format('svg'); /* Legacy iOS */
}
</style>
<!-- Material Icons -->
<style>
@font-face {
  font-family: 'Material Icons';
  font-style: normal;
  font-weight: 400;
  src: url(${contextPath}/static/fonts/icons/MaterialIcons-Regular.eot); /* For IE6-8 */
  src:
    local('Material Icons'),
    local('MaterialIcons-Regular'),
    url(${contextPath}/static/fonts/icons/MaterialIcons-Regular.woff2) format('woff2'),
    url(${contextPath}/static/fonts/icons/MaterialIcons-Regular.woff) format('woff'),
    url(${contextPath}/static/fonts/icons/MaterialIcons-Regular.ttf) format('truetype');
}

.material-icons {
  font-family: 'Material Icons';
  font-weight: normal;
  font-style: normal;
  font-size: 24px;  /* Preferred icon size */
  display: inline-block;
  line-height: 1;
  text-transform: none;
  letter-spacing: normal;
  word-wrap: normal;
  white-space: nowrap;
  direction: ltr;

  /* Support for all WebKit browsers. */
  -webkit-font-smoothing: antialiased;

  /* Support for Safari and Chrome. */
  text-rendering: optimizeLegibility;

  /* Support for Firefox. */
  -moz-osx-font-smoothing: grayscale;

  /* Support for IE. */
  font-feature-settings: 'liga';
}

</style>
<style>
	body{
		font-family: "Roboto", "Helvetica", "Arial", sans-serif;
		background-color: #FFF !important;
	}
</style>
</head>
<body>
	<div id="main-app-container"></div>
	<script>
		window.__GLOBAL_CONFIG__ = ${globalConfig};
	</script>
	<script src="${contextPath}/static/built/bundle.js"></script>
</body>
</html>