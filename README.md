# adapters

This is a collection of adapters which have previously shipped with Diffusion, but now are available separately.

Diffusion is a high-performance message broker optimised for distributing messages to a large number of
connected clients. See the [Push Technology](http://www.pushtechnology.com) website for more information.

**NOTE:**
To compile these adapters, you must provide a compatible copy of diffusion.jar (e.g. from the 4.5.4_01 release),
and place it in the lib directory. For the CDC adapter, you must also supply ifxjdbc.jar for the Informix JDBC
adapter; see the licence section.

The following adapters are available:

- Twitter
- RSS
- Mail
- CDC
- C2MD
- APND
 
Each adapter can be built independently, for example:

    ant twitter
    
The default ant target will build all adapters except for the CDC adapter, due to its proprietary library requirements.

## Licence

These adapters are licensed under the Apache 2.0 licence.

Some adapters rely on 3rd party libraries; specifically:

 * The Apache Commons HTTP client library [Apache 2.0 licence](http://www.apache.org/licenses/LICENSE-2.0.txt)
 * JSON.org [licence](http://www.json.org/license.html)
 * JavaMail [CDDL 1.0 licence](http://opensource.org/licenses/CDDL-1.0)
 * Informix JDBC driver (Proprietary, available from [IBM](http://publib.boulder.ibm.com/infocenter/idshelp/v10/index.jsp?topic=/com.ibm.jdbc_pg.doc/jdbc32.htm))
