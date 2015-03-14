# Checkout #

You will need:
  * Jboss AS6
  * Eclipse (Helios)

After extracting Jboss AS6:
  * Copy mysql and jdom jars to `<jboss_path>/common/lib`.
  * Copy ahy-cms-ds.xml to `<jboss_path>/server/default/deploy` and edit it to check database config.

Do a checkout for each project: ahy, ahy-kernel and ahy-web.

Add a new server in Eclipse, pointing to `<jboss_path>`, check the target runtimes of all three projects.

On ahy-web, change the path of jsf-api.jar (project properties/build path).

... to be continued ...