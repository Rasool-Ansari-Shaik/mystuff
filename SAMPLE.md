# Engagement Web UI Technical Design
The document contains the architectural and technical investigations conducted and decisions to guide the technical implementation of engagement Web UI.

## Table of Contents
1. [Overview](#overview)
2. [Atomic design](#atomic-design)
3. [Technical Design](#technical-design)
4. [App Framework for user management](#app-framework-for-user-management)
5. [Tech Stack](#tech-stack)
6. [CI and CD](#ci-and-cd)
7. [SPA VS Webapp Using React](#spa-vs-webapp-using-react)
8. [I18N and L10N](#i18n-and-l10n)
9. [NPM vs YARN](#npm-vs-yarn)
10. [Scalability](#scalability)
11. [Error and Exception Handling](#error-and-exception-handling)
12. [Application Performance Monitoring](#application-performance-monitoring)
13. [Accessibility standards -- Pending From Business](#accessibility-standards)
14. [Browser compatibility](#browser-compatibility)
15. [Personalization -- Pending From Business](#personalization)
16. [Whitelabeling capability -- Pending From Business](#whitelabeling-capability)
17. [Configurability](#configurability)
18. [Routing](#routing)
19. [Automated testing](#automated-testing)
20. [State Management](#state-management)
21. [Support of multiple instances of the same organism on a single template](#support-of-multiple-instances-of-the-same-organism-on-a-single-template)
22. [Responsive Design and Resolution -- Pending From Business](#responsive-design-and-resolution)

## Overview
Atmosphere UI in the legacy platform is an asp.net based application. It consists of the following business capabilities.
- Outreach
- Coordinate
- Remind
- Transition

As part of the modernization roadmap, Atmosphere to re-architecture leveraging modern web technologies, trends, and improved user experience. Based on guidance from the IBM EA council, ReactJS was selected as the framework to build the new UI while carbon design system to dictate the user experience across the application.

## Atomic design
One of the critical architectural objectives is to design the new platform to be extensible with features with minimum development, testing, and implementation efforts.

Atomic design methodology, first introduced by Brad Frost, is a methodology that suits well with the architectural objectives. It guides the development application in a well structured and modular manner while enforcing the reusability and web component guidelines.

Atomic design is a methodology composed of five distinct stages working together to create interface design systems in a more deliberate and hierarchical manner. The five stages of atomic design are:


| Stage | Example |
|------|------|
| **Atoms** | Textbox, Checkbox, Label |
| **Molecules** | Breadcrumb, Number input form field |
| **Organisms** | Grid with sorting and filtering, Left navigation |
| **Templates** | Two column page layout, Three column page layout |
| **Pages** | Home page, dashboard page, Outreach | 


 > Read more at https://atomicdesign.bradfrost.com

### Atomic design for engagement UI
IBM Carbon design systems provide many of the Atoms and Molecules required to design the new engagement UI. Based on the conducted research, implementation will need building Organisms, Templates, and Pages to implement requirements defined in the requirement backlog.

All components developed while following Atomic methodology should function independently and must not contain the knowledge of other components.

- The Atomic Design should have a file of variables, and each component must import it
- When developing atoms, do not configure margins or positions. Higher-level components such as organisms and molecules to manage the rendering position for the atom
- The template defines the underlying structure of how a page can place components and should not contain any functional components
- When creating a page, a template must be associated.

### Guidelines for design and development of an organism
- Project structure for a component - TODO
- The output of an organism must be a reusable node package and does not contain any knowledge of business logic related to engagement modernization.
- Each organism deployable package must publish to npm repository in jFrog
- The organism must support data binding and configurations as parameters.
- Implementation of an organism must support operating multiple instances of the organism in a single page. 
- Must include I18N and L10N infrastructure and support

```
- Component.js
- Component.scss
- Component.test.js
- Component.stories.js
```
> Minimal code structure of an organism


```javascript
const ReportsTemplate = ({actions, children}) => {
	return (
		<div>
			{actions && <div className="TemplateActionsContainer">{actions}</div>}
			<div className="TemplateTableContainer">{children}</div>
		</div> 
	);
};

export default ReportsTemplate;
```
> Sample template


```javascript
function PatientReportsPage(){
  const Actions = () => {
	  return (
		<div>
			// Actions todo
		</div>
	  );
  };
   
  return (
	<ReportsTemplate actions={<Actions/>}>
		<Patients/>
	</ReportsTemplate>
  );
}
```
> Sample page

## Technical Design
Following is the high level design for the engagement UI

![High level technical design](./images/ui-high-level-design.png)

- Web stack - ReactJS
- User authentication, authorization - IBM AppId
- Application performance monitoring - NewRelic

### High Level data Flow
Engagement UI will consume data from REST endpoints directly exposed by the IBM FHIR server or through a facade layer. Further explorations are required to understand the performance implications of each option. 

![High-level data flow](./images/ui-data-services1.png)

### Proposed Data Flow
As part of data flow research, we have explored IBM API connect. Shown below are two proposed flow diagrams, to retreive data from FHIR. The below diagram lists the capabiities of API connect, that we can utilize for Engagement UI.

API Connect Exploration: As part of API connect research, we have implemented a data translation for get and post requests.

#### Proposed Flow Design 1
![Proposed Backend Data Flow 1](./images/proposed-design-1.PNG)

#### Proposed Flow Design 2 - Finalized
![Proposed Backend Data Flow 2](./images/proposed-design-2.PNG)

> Read more: [IBM API Connect](https://github.ibm.com/IBM-WH-ENGMNT/poc-modernizationui/blob/develop/ModernizationReactSPA/Documentation/IBM_API_Connect_Documentation.md)

#### Observations from research on Read FHIR data
FHIR server APIs generate data in FHIR JSON format. While it is possible to bind any JSON formatted data in the web UI, we need to implement data processing logic in the frontend controllers to transform FHIR JSON into a JSON format compatible with React components. The following are some of the challenges if the UI is bound to FHIR server APIs.
- FHIR JSON payload is about 2x times larger than the data payload required by UI components
- If the frontend implements business logic to transform data, then it may introduce performance issues depends on user PC configurations.

Recommendation: implement a facade to transform FHIR JSON to a lightweight JSON, which can bind to React components without additional data transformation logic.

#### Observations from research on Write FHIR data
To write data on to the FHIR DB, the API expects the input data in FHIR format. The data from the web UI will need to be transformed into the FHIR format before appending to the FHIR POST API.

Recommendation: Ths transformation operation could be a data heavy operation for the UI and it is recommended to do the same on a mid-tier layer.

> [Data Comparison](https://github.ibm.com/IBM-WH-ENGMNT/poc-modernizationui/blob/develop/ModernizationReactSPA/Documentation/read-write-data-using%20fhir.md)

### AuthN/AuthZ Data Flow
Engagement web will use IBM App ID to implement user authentication and authorization and App Framework for user and permission management.

Limitations identified in JavaScript SDK for App ID
- No out of the box capability to retrieve roles assigned to a user in App ID cloud directory

![The authorization flow of single-page applications](./images/spa.png)

> https://cloud.ibm.com/docs/appid?topic=appid-single-page

#### Steps to fetch user roles
Since the limitation with App ID JavaScript SDK, retrieving user roles for the authorization must implement on top of SDK capabilities.

- User is authenticated
- After authentication a token is fetched from the IAM service
- The IAM token is sent in the header to the AppID service to fetch the Roles of the logged in User

![AuthN/AuthZ Data Flow](./images/auth-data-flow.png)

## App Framework for user management
Watson Health has an application framework that provides the following capabilities:
- Launch Page
- User Management (users and roles)
- Self services for users (reset poassword, forget password, etc)

The user profile creation and role assignment for the Engagement UI users will be taken care from the User Management Interface provided by the App Framework. 

Users' journey will start from a portal hosted by App Framework. Once the user is authenticated and landed in the main portal if the user has the authorization to access the engagement platform, a link to engagement UI will be visible.

Users can click on the link to launch the engagement UI, and with App ID SSO, the user will have a transparent user authentication into the engagement platform.

### Further Research Area
We would be retreiving the data required for Engagement UI from the FHIR server. As per the research done we are assuming that there woud be a facade between the FHIR server and the UI. This facade would authenticate the request from UI using AppID before fetching data from FHIR. This would help in achieving AuthN/AuthZ at the data level. Further discussions and research is needed to understand how AuthN/AuthZ would work at the data level and how we can integrate facade to AppID. Java teams would be carrying out this research item.

### Onboarding Users & Roles (until APP Framewrok for user manage is ready)
- Profiles
We can add custom users and add their profiles to the AppID. Please find the below screenshot for reference.

![Profiles](./images/profiles.PNG)

- Roles
We can create custom roles to the user profile as below:

![Roles](./images/Roles.PNG)

> [AuthN/AuthZ for Engagement UI](https://github.ibm.com/IBM-WH-ENGMNT/poc-modernizationui/blob/develop/ModernizationReactSPA/Documentation/AppID.md)


### Technical Findings
- It is recommended fetch the roles, custom attributes from the Mid-Tier.

### References 
- [Authorization](https://us-south.appid.cloud.ibm.com/swagger-ui/#/Authorization%20Server%20-%20Authorization%20Server%20V4)
- [AppID](https://cloud.ibm.com/docs/appid?topic=appid-about)
- [AppID Customizing Tokens](https://cloud.ibm.com/docs/appid?topic=appid-customizing-tokens)
- [Securing Single-page-apps](https://www.ibm.com/cloud/blog/securing-single-page-apps-app-id-service)

## Tech Stack
Following are the technologies/libraries we would be using to build the UI App.

| Technology/Library |  Usage | License | IBM Pedigree Approval | Link | Decision | Dependency |
|----------|-------------|------|--|--|--|--|
| Node | As a platform to run the application | MIT license | Approved | [Node](https://nodejs.org/en/) | Confirmed | NA |
| NPM/Yarn | Package managers | Artistic License 2.0//BSD 2-Clause License | Approved/Approved | [NPM](https://www.npmjs.com/)/[YARN](https://classic.yarnpkg.com/en/) | TBD - As far as implementation goes, any Package manager is okay to work with. | NA |
| ReactJS  | To build the UI | MIT License | Approved | [ReactJS](https://reactjs.org/) | Confirmed | Production Dependency |
| Carbon-UX | IBM theming and component library | IBM | Approved | [CarbonDesignSystems](https://www.carbondesignsystem.com/) | Confirmed | Production Dependency |
| Typescript | Programming Language |  |  | [Typescript](https://www.typescriptlang.org/) | Confirmed | Production Dependency |
| ESLint | JS/TS Code Linting | MIT License | Approved | [ESLint](https://eslint.org/) | Suggested | Dev Dependency |
| Prettier | Code Formatter | MIT License | Approved | [Prettier](https://prettier.io/) | Suggested | Dev Dependency |
| SASS | Styling Support | MIT License | Approved | [SASS](https://sass-lang.com/) | Suggested | Production Dependency |
| Jest/Mocha | Unit testing | Facebook/[Creative Commons Attribution 4.0 International License](https://creativecommons.org/licenses/by/4.0/) | Approved/Needs legal approval for product or service usage, ok for internal use | [Jest](https://jestjs.io/)/[Mocha](https://mochajs.org/) | Jest is Suggested, as this is used by Carbon-UX and this also pedigree approved. | Dev Dependency |
| Istanbul JS | Unit test coverage report | MIT License | Approved | [Istanbul JS](https://istanbul.js.org/) | Suggested | Dev Dependency |
| Jest-stare | Regression testing for cosmetic issues | MIT License | Approved | [Jest-Stare](https://www.npmjs.com/package/jest-stare) | Suggested | Dev Dependency |
| Axios | To make asynchronous calls to backend DATA API | MIT License | Approved | [Axios](https://www.npmjs.com/package/axios) | Confirmed | Production Dependency |
| I18Next/Globalize  | Internationalization Library | MIT License/MIT License | Approved/Approved | [I18Next](https://www.i18next.com/)/[Globalize](https://www.npmjs.com/package/globalize) | Globalize is suggested as this does not require any external library to do regional specific changes - such as currency/mertics translation. | Productoin Dependency |
| Convert-units | Localization Library | MIT License | Approved | [Convert-Units](https://www.npmjs.com/package/convert-units) | This is required only if I18Next is used for Internationalization. | Production Dependency |
| Redux/React-Context | As UI data store | MIT License/MIT License | Approved/Approved | [Redux](https://redux.js.org/)/[React-Context](https://reactjs.org/docs/context.html) | Redux is suggested, as it helps solve the "State persistence" issue in ReactJS - Issue: React state is lost when the browser is refreshed. | Production Dependency |
| Redux-persist | As UI data store persistance library | MIT License | Approved | [Redux-Persist](https://www.npmjs.com/package/redux-persist) | Suggested | production Dependency |
| Storybook | Component level visual testing library | MIT License | Approved | [Storybook](https://storybook.js.org/) | Suggested | Dev Dependency |
| JFrog | UI artifactory container repository |  | Approved | [JFrog](https://na.artifactory.swg-devops.com/artifactory/webapp/#/home) | Confirmed | NA |
| IBM COS/Kubernetes | Application will be hosted here |  | Approved |  | TBD | NA |
| NewRelic | Application Performance Monitoring |  | Approved | [NewRelic](https://newrelic.com/) | Confirmed | NA |
| App Framework | Authorization and Authentication |  | Approved |  | Confirmed | NA |
| LaunchDarkly/ Optimizely/ Rollout/ Manual Implementation | Feature Flagging - All are Commercial Software | Apache V2/Apache V2/NA | Approved/Approved/NA | [Launch Darkly](https://launchdarkly.com/)/[Optimizely](https://www.optimizely.com/)/[Rollout](https://rollout.io/) | Suggested | Production Dependency |

## CI and CD
### JFrog Setup
- We would use JFrog as the repository to hold the artifacts for Engagement UI.
- The following repos have been created in JFrog.
```
Local Repository: To hold the reusable Artifacts created for Engagement UI.
Remote Repository: To connect the with publicly published repositories suc as NPM.
Virtual Repository: To consume both Local and remote repositories. This repository will be used for the development purposes as a single source to provide both the external libraries and reusable artifacts created for engagement UI.
```
![JFrog Repos](./images/jfrog-repos.PNG)

We will use WHC toolchain to build the CI/CD pipeline for UI. 

### CI Pipeline
![CI Pipeline](./images/modernization-ui-ci.png)

### CD Pipeline
![CD Pipeline](./images/modernization-ui-cd.PNG)

## SPA VS Webapp Using React
As part of the integration with AppID we came to know that, integration of AppID with webapp - server rendered application (SSR) - works better than with an SPA. As part of this research we have done our research on NextJS.

### NextJS
NextJS is a popular and lightweight framework for static and server‑rendered applications built with React.

### Features
- Pre-Rendering
- Static Exporting
- CSS-in-JS
- Automatic code splitting
- Filesystem based routing
- Hot code reloading
- Universal rendering
- Fully Extensible
- Ready for Production
> Read More: [NextJS](https://nextjs.org/)

Recommendation: NextJS provides a way to create a webapp using React. But at this point the recommendation would be to go with an SPA, as state management seems to be a tedious effort with NextJS - refer to JamisCharles tutorial.

## References
- [NextJS Docs](https://nextjs.org/docs/getting-started)
- [NextJS Reading JamisCharles](https://medium.com/@jamischarles/lessons-learned-with-next-js-change-title-6423b2f2ab8d)
- [NextJS Further Reading](https://stackshare.io/next-js)

## I18N and L10N
### Internationalization
Internationalization is a design process that ensures a product (usually a software application) can be adapted to various languages and regions without requiring engineering changes to the source code.

### Localization
- Localization (also referred to as "l10n") is the process of adapting a product or content to a specific locale or market. 
- Translation is only one of several elements of the localization proces.
- It includes converting to local requirements (such as currencies and units of measure or even date and time formats).

### Implementation of I18N and L10N in Engagement UI
As part of the implementation the idea is to maintain the same code base for multiple regions while implementing I18N/L10N. To achieve this we have to build seperate language packs for different regions on which the application will be made available. These language packs would then be injected into the application. And based on the locale for the region, the application would pickup the specific language pack and do the translation of the page.

### Suggested Libraries
I18Next and Globalize are a couple of popular libraries to implement I18N and L10N.

|  | I18Next |  Globalize |
|--|--------------------|--------|
| Regional Data Formating | Requires additional libraries; - Date formatter : Moment. - Convert-Units - Currency, Weight and other metrics | Doesn't require external liraries |
| Support Language Packs | Yes | Yes |
| Automatic Language Detection - Browser setting | Yes | To research |

### Points to consider while implementation
- The required initial set-up/configuration files and language packs are to be loaded at project level.
- Applying the language packs and data conversion to match regional preferences is to be done at the component level.
- I18N/I10N can be achieved in any of the following ways.
```
- Language settings are automatically read from the browser settings
- Changing language settings using a dropdown selection on the UI
- Providing regional modifiers in the URL
```

Recommendation: It is recommended to use the Globalize library as it is a complete product, providing all the capablities required to achieve I18N/L10N. Also this library is still evolving and including more capabilities to support regional data changes.

### References
- [Internationlization](https://jsw.ibm.com/browse/WHOUTREACH-582)
- [Localization](https://github.ibm.com/IBM-WH-ENGMNT/poc-modernizationui/blob/develop/Localization_Changes_spec.md)
- [I18N](https://medium.com/rd-shipit/internationalizing-a-front-end-application-88f1baae3d82)
- [I18Next](https://medium.com/@jishnu61/6-easy-steps-to-localize-your-react-application-internationalization-with-i18next-8de9cc3a66a1)

## NPM vs YARN
Yarn is a new package manager for JavaScript developed by Facebook engineering. It was developed to address some of the shortcomings of the industry defacto JavaScript package manager NPM.

Following is a comparison of Yarn with NPM to help the decision to select a JavaScript package manager for the Outreach UI project.

### YARN Features
- Offline download
- Parallel Installation
- strongly binds package versions
- License checks
- Support - Can create a Yarn project out of a NPM application - Easy transition from NPM to Yarn

### YARN disadvantages compared to NPM
- Problems with installing native modules

> Read More: [NPM VS YARN Comparison](https://github.ibm.com/IBM-WH-ENGMNT/architecture/blob/web-ui-analysis/components/web-ui/NPM%20VS%20Yarn%20Comparison.md)

## Scalability 
We are exploring two ways to host the UI application - Kubernetes and COS. Each of these has their own way of handling incoming traffic.

### Hosting on Kubernetes
Kubernetes takes care of the resource allocation depending on the incoming traffic for the application dynamically.

### Hosting on COS
If we host the application on COS, we have to add a CDN which will provide the caching ablity for the application, improving the performance. But as of now the CDN service is not approved by the IBM security council. And there is a discussion going on to get it approved. 

### Disaster recovery
As part of disaster recovery plan, if the decision is taken to use multiple instances, then a load balancer can be used along with kubernetes.
> Read more [Load Balancing on Kubernetes](https://kubernetes.io/docs/concepts/services-networking/)

For further info on application scalability for UI features please refer the following link.
> Read more: [Scaling React Apps](https://www.smashingmagazine.com/2016/09/how-to-scale-react-applications/)

## Error and Exception Handling
The monitoring tool NewRelic, monitors runtime errors and logs them. These can be monitored from the NewRelic dashboard and appropriate action can be taken by the support teams monitoring the application.

For any business errors/exceptions, we would maintain a config file and error templates.

## Application Performance Monitoring
We are using NewRelic as a Performance Monitoring tool for our UI application. NewRelic Browser gives you full visibility into the complete web page life cycle of modern web applications. With Browser, you gain deep insight into how your users are interacting with your application or website. Browser measures page load timing, also known as real user monitoring, but it goes far beyond that to measure:
- Individual session performance
- AJAX requests
- Route changes in apps with single-page application (SPA) architecture
- JavaScript errors
- Apdex user satisfaction scores

This tool could be beneficial in application support activities. The support analysts can monitor the tool for any issues/errors or any peformance lags and take action for the remediation, even before any ticket is raised for the same issue.

As part of OOTB integration of NewRelic with Modernization UI SPA, the following steps are to be followed.
- Go to rpm.newrelic.com/browser  > (select an app) > Settings > Application settings, then select Pro + SPA.
- Agree to the Terms of Service.
- Select Save application settings.
- Deploy the new JavaScript snippet - Copy/paste method: Copy the entire JavaScript code snippet. Paste it as close to the top of the HEAD of your webpage as possible, but after any position-sensitive META tags (for example, X-UA-Compatible or charset information).

There is further advanced integration we can do with NewRelic. There is one SPA API provided that can be explored for advanced data analytics.

### References
- [NewRelic SPA Monitoring](https://docs.newrelic.com/docs/browser/single-page-app-monitoring/get-started/introduction-single-page-app-monitoring)
- [SPA API](https://docs.newrelic.com/docs/browser/new-relic-browser/browser-agent-spa-api)

## Accessibility standards
IBM prescribes some basic standards for accessibility. The IBM Equal Access Accessibility Checker is an open source tool provided by IBM for web developers and auditors that utilizes IBM's accessibility rule engine, which detects accessibility issues for web pages and web applications. The extension integrates into the browser development tools, providing an integrated checking experience, helping users quickly identify the source of accessibility issues and try fixes. 

We can leverage the below tool while developing the engagement UI. Also for further reading on IBM accessibility standards have a look at the references section.

> Install [IBM Equal Access Checklist](https://chrome.google.com/webstore/detail/ibm-equal-access-accessib/lkcagbfjnkomcinoddgooolagloogehp)

### Further discussion
Yet to receive any requirements from business.
> Updated On  - 24th June 2020

### References
- [IBM Accessibility](https://www.ibm.com/able/)

## Browser compatibility
As per the implementation plan we would support the following browsers. 
- Chrome
- Firefox
- Safari
- Edge

> Read more: [Decisions on Web UI](https://github.ibm.com/IBM-WH-ENGMNT/architecture/blob/master/components/web-app.md)

## Personalization
As part of personalization, we are handling themes on the UI. Carbon UX provides four themes as below.
- White 
- gray 10
- gray 90
- gray 100

Carbon also allows the customization of these themes. PLease refer to Carbon-UX themes for further reading.

### Further Discussion
We are yet to receive any requirements regarding personalization/customization from the business.
> Updated On  - 24th June 2020

### References
- [Carbon-UX Themes](https://www.carbondesignsystem.com/guidelines/themes)
- [Personalization VS Customization](https://instapage.com/blog/customized-vs-personalized?utm_medium=ppc&utm_source=adwords&utm_term=&device=c&network=g&matchtype=b&campaign=&utm_agn=&campaignid=9557948367&adgroupid=101783366767&adid=423056473831&gclid=EAIaIQobChMIkr3c-bfy6QIVzMDACh3yPgjoEAAYASAAEgKPnvD_BwE)

## Whitelabeling capability
We have not received any requirements for Whitelabling from the OM team yet. 
> Updated On  - 24th June 2020

### Further discussion 
Further discussion needs to be done, on how we can structure our project for any such requirements we might receive.

## Configurability
As part of this we want to dynamically add/remove features to the Engagement UI in different testing environments without any code changes. This would help in effective testing our application with a selected group of users. This way when the feature is made available to a larger audience we can be sure of the quality. To achieve this we can use a technique called feature Flags.
### What are feature flags?
Feature flags allow you to slowly roll out a feature gradually rather than doing a risky big bang launch and are extremely helpful when used in a continuous integration and continuous delivery environment.

### Avaliable Libraries to implement feature flags in ReactJS?
There are different approaches avaliable to implement feature flag. Out of them below are used by most of the  companies.
#### OpenSource
- There are no open source avalaible in ReactJS to implement feature flag.
- There are some other open sources avaliable for Angular, dotnet, java and other frame works. Reference link 'https://featureflags.io/resources/'

#### Commercial 
- Launchdarkly
- Optimizely
- Rollout

Among those 3 we suggest launch darkly is prefered approach to implement feature flags.
To find detailed comparison please have a look at references section.

### Implementation of feature flags in ReactJS Using launch Darkly
- Login/signup to LaunchDarkly.
- Create a feature in the LaunchDarkly Dashboard. This provides us with a "Client-ID" and a "User-Key".
- Launch darkly allows customization to different environments like test/production based on their requirements. This can be done on the LaunchDarkly dashboard.
- Install react-luanch-darkly library through npm.
- Include "launchdarkly" as a provider in the App component and map it to the account using the "Client-ID" and the "User-Key" from Step-2.
- Perform feature toggling at the component level and navigation level using library methods like "renderFeaturecallback" with the "Feature-Name" from the Step-2.
    
### References 
- [Launch darkly feature flag documentation](https://docs.launchdarkly.com/home/getting-started/feature-flags)
- [Featureflag creation in launch darkly website](https://app.launchdarkly.com/default/test/features)
- [Feature flags Comparison documentation](https://github.ibm.com/IBM-WH-ENGMNT/architecture/blob/web-ui-analysis/components/web-ui/Feature%20Flag%20Analysis.md)

## Routing
### Client-side Routing
The modern UI frameworks support a technique called "client-side-routing". Any Framework/Library implementing this is said to follow the following.
- A route change will not reload the page but will load new content on the same page based on the route.
- The forward/back navigation buttons, move back or forward through the routes that were previously visited.

ReactJS has its own implementation of the same by means of the router module. Please have a look at "ReactJS Routing" in references section for further reading.

### Further discussion
- Query parameters security

### References
- [ReactJS Routing](https://reacttraining.com/react-router/web/example/url-params)

## Automated testing
As part of automated testing for Modernization UI we would be explored the following.
- Unit testing
- Regression testing at component level

### Unit testing
As part of research we have looked into Jest and Mocha to perform unit testing.

#### Mocha 
Mocha is a feature-rich JavaScript test framework running on Node.js and in the browser, making asynchronous testing simple and fun. Mocha tests run serially, allowing for flexible and accurate reporting, while mapping uncaught exceptions to the correct test cases.
- Serial test-case execution
- Covers Unit-testing, Integration testing and End-to-end Testing 
#### Jest
Jest is a JavaScript Testing Framework, that run tests in parallel. To make things quick, Jest runs previously failed tests first and re-organizes runs based on how long test files take.
- Parallel test-case execution
- Covers Unit testing

Please have a look at references for more info on Jest VS Mocha. 

Recommendation: We recommed to use Jest, because of its faster execution of the test cases and also as it is already being used by the Carbon Library.

### Regression Testing - Cosmetic issues
We have explored a library Jest-Stare for this. By using this library we can do a side-by-side comparison of the original UX wireframe and the one that the developers have come up with. This would help greatly in identifying the UI cosmetic issues before we provide the application to the QA for testing considerably reducing the number of unidentified defects.

![Jest Stare Example Screenshot](./images/Jest-Stare.png)

### References
- [Jest VS Mocha](https://knapsackpro.com/testing_frameworks/difference_between/jest/vs/mochajs)
- [Migrating to Jest from Mocha](https://medium.com/airbnb-engineering/unlocking-test-performance-migrating-from-mocha-to-jest-2796c508ec50)
- [Jest-stare](https://www.npmjs.com/package/jest-stare)
- [Puppeteer for Regression Testing](https://pptr.dev/)

## State Management
### State
State is the place where the data comes from. We should always try to make our state as simple as possible and minimize the number of stateful components. If we have, for example, ten components that need data from the state, we should create one container component that will keep the state for all of them.
> Read more: [State - Tutorials Point](https://www.tutorialspoint.com/reactjs/reactjs_state.htm#:~:text=State%20is%20the%20place%20where,state%20for%20all%20of%20them.)

### Important points to consider when implanting state management in React application
- State management in ReactJS can be done in couple of ways - Redux or ReactJS Context.
- This can be done at application level or at the component level.
- Multiple instance of an organism need a way to save their state against an Instance-ID for that organism. Instance-ID can be dynamically generated from the page that renders the organism.
- React State is not persistent. The State is lost when the page refreshes. 
- Redux and React-Context API both can be used with Functional as well as Class components.
- Organisms can be built to support different themes as per the branding requirements. Since we dont see a need for multiple brands - no requirements - we will not implement capabilities to brand organisms differently and it will inherit global branding.

### Redux VS React-Context
|  | Redux | React-Context |
|--|-------|---------------|
| External Library Required | Yes | No. Internal to ReactJS |
| State Persistence | Has a complimenting library Redux persistence. | Need to use session storage to save state. |

Recomendation: Both Redux and React-Context provide us with all the tools we need. With te exception being that Redux has a Complimenting Persistence library. We recomend using Redux over the Context library, as Redux is more evolved over Context and is widely used as of today.

### References 
- [State management in ReactJS](https://tsh.io/blog/react-state-management-react-hooks-vs-redux/#:~:text=React%20state%20management%20%E2%80%93%20why%20is,result%20of%20the%20user's%20action)
- [Redux-persist](https://www.npmjs.com/package/redux-persist)
- [Redux VS Context](https://blog.softwaremill.com/react-context-api-vs-redux-the-eternal-dichotomy-24639907fc98)
- [Middleware: Redux-saga VS Redux-thunk](https://medium.com/@shoshanarosenfield/redux-thunk-vs-redux-saga-93fe82878b2d)

## Support of multiple instances of the same organism on a single template
### Rendering a State-less Organism
A state-less organism can be easily rendered multiple times on a single page. The page that is handling multiple instances of the organism, should pass different data to it and it would render accordingly. 

### Rendering a Stateful Organism
Rendering a stateful organism would be more difficult to handle. All the stateful data associated with the instance, should be saved in the state, with an "InstanceID", linking it to the instance of the organism. So that when there are any changes to the state, the instance can look for any changes that might pertain to it and re-render accordingly.

![Organism Multiple Instance](./images/organism-multiple-instance.PNG)

#### Steps to render a Stateful Organism multiple times
- Page to provide an instance-ID to the organism while instantiation 
```javascript
	<ReportsTemplate actions={<Actions/>}>
		<Patients 
			data={data1}
			id="PL1"
		/>
		<Patients 
			data={data2}
			id="PL2"
		/>
	</ReportsTemplate>
```
- Organism to dispatch the data to store along with instance-ID
```javascript
	dispatch({ type: 'PATIENT_DATA', payload: {"id":props.id, 'list':props.data} });
```
- Store to save the data against the instance - preferably as a list of following sample object
```javascript
	{"id":id, 'list':data}
```
- Any update to the state (data) happening on the page, to be dispatched to the store, along with instance-ID
- The organism to filter the data as per the instance-ID before binding it to the HTML - sample filtering below
```javascript
	let patientHeaderlist = useSelector(state => state.patientHeaderlist);
	let tempList = [];
	if(patientHeaderlist && Array.isArray(patientHeaderlist) && patientHeaderlist.length > 0){
		patientHeaderlist.forEach(item => {
			if(item.id == props.id) 
				tempList = item.list;
		});
	}
	patientHeaderlist = tempList;
```

## Responsive Design and Resolution
As per the current implementation plan, only the desktop view of the application is in scope. However as we are using the CarbonUX component library - carbon components support responsive design, we can build/support mobile and tablet views with minimal code changes.
> Updated On  - 24th June 2020
