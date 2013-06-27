adapters
========

This is a collection of adapters which have previously shipped with Diffusion, but now are available separately.

**NOTE:**
To compile these adapters, you must provide a compatible copy of diffusion.jar (e.g. from the 4.5.4_01 release), and place it in the lib directory.

The following adapters are available:

- Twitter
- RSS
- Mail
- CDC
- C2MD
- APND
 
Each adapter can be built independently, for example:

    ant twitter
    
The default ant target will build all adapters.
