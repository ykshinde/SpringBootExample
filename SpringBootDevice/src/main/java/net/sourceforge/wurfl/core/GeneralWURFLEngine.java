/**
 * Copyright (c) 2014 ScientiaMobile Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Refer to the COPYING file distributed with this package.
 */
package net.sourceforge.wurfl.core;


import java.net.URL;
import java.util.Collection;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.wurfl.core.cache.CacheProvider;
import net.sourceforge.wurfl.core.exc.WURFLRuntimeException;
import net.sourceforge.wurfl.core.matchers.MatcherManager;
import net.sourceforge.wurfl.core.request.DefaultWURFLRequestFactory;
import net.sourceforge.wurfl.core.request.UserAgentResolver;
import net.sourceforge.wurfl.core.request.WURFLRequest;
import net.sourceforge.wurfl.core.request.WURFLRequestFactory;
import net.sourceforge.wurfl.core.request.WURFLRequestFactoryWithPriority;
import net.sourceforge.wurfl.core.resource.DefaultWURFLModel;
import net.sourceforge.wurfl.core.resource.WURFLModel;
import net.sourceforge.wurfl.core.resource.WURFLResource;
import net.sourceforge.wurfl.core.resource.WURFLResources;
import net.sourceforge.wurfl.core.resource.XMLResource;
import net.sourceforge.wurfl.core.web.WurflWebConstants;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component(value="WURFLEngine")
public class GeneralWURFLEngine implements WURFLEngine, WurflWebConstants {
	private final transient Logger logger = LoggerFactory.getLogger(getClass());
    private String[] capabilityFilter = null;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private WURFLResource root = null;
    private WURFLResources patches = null;

    private MarkupResolver markupResolver;
    private CapabilitiesHolderFactory capabilitiesHolderFactory;
    private DeviceProvider deviceProvider;
    private CacheProvider cacheProvider;
    private WURFLService wurflService;
    private WURFLRequestFactory wurflRequestFactory;


    private UserAgentResolver userAgentResolver;
    private WURFLUtils wurflUtils;
    private WURFLModel model;
    private Boolean engineInitilized = new Boolean(false);

    private EngineTarget desiredEngineTarget = null;
    private UserAgentPriority userAgentPriority = UserAgentPriority.OverrideSideloadedBrowserUserAgent;
    
    static URL filePath = GeneralWURFLEngine.class.getClassLoader().getResource("wurfl.zip");
    
    public GeneralWURFLEngine() {
    	this(new XMLResource(filePath.getPath()));
	}

    public GeneralWURFLEngine(String root) {
        this(new XMLResource(root));
    }

    /**
     * Creates a {@link GeneralWURFLEngine} specifying the root {@link WURFLResource} and
     * no patch
     * @param root the {@link WURFLResource} identifying the main wurfl database
     */
    public GeneralWURFLEngine(WURFLResource root) {
        this(root, (WURFLResources) null);
    }

    public GeneralWURFLEngine(String rootPath, String... patchesPath) {

        WURFLResource root = createResource(rootPath);
        WURFLResources patches = createResources(patchesPath);

        Validate.notNull(root, "The root resource is null");

        this.root = root;
        this.patches = patches;
    }

    /**
     * Creates a {@link GeneralWURFLEngine} specifying the root {@link WURFLResource} and
     * as much as {@link WURFLResource}s are needed as patches.
     * @param root the {@link WURFLResource} identifying the main wurfl database
     * @param patches the {@link WURFLResource}s identifying the patches
     */
    public GeneralWURFLEngine(WURFLResource root, WURFLResource... patches) {
        this(root, new WURFLResources(patches));
    }

    /**
     * Creates a {@link GeneralWURFLEngine} specifying the root {@link WURFLResource} and
     * the {@link WURFLResources} patches list.
     * @param root the {@link WURFLResource} identifying the main wurfl database
     * @param patches the {@link WURFLResources} identifying the patches list
     */
    public GeneralWURFLEngine(WURFLResource root, WURFLResources patches) {

        Validate.notNull(root, "The root resource is null");

        this.root = root;
        this.patches = patches;

    }

    /**
     * Creates a {@link GeneralWURFLEngine} with a pre-existing {@link WURFLModel}
     * @param model the {@link WURFLModel} instance
     */
    public GeneralWURFLEngine(WURFLModel model) {
        this.model = model;
    }
    
    /**
     * {@inheritDoc}
     */
    public void reload(String rootPath) {
        WURFLResource root = createResource(rootPath);
        reload(root, (WURFLResources) null);
    }
    
    /**
     * {@inheritDoc}
     */
    public void applyPatches(String... patchesPath) {
        if (patchesPath == null) {
            logger.warn("null patches, do nothing...");
            return;
        }

        applyPatches(createResources(patchesPath));
    }
    
    /**
     * {@inheritDoc}
     */
    public void applyPatches(WURFLResource... patches) {
        applyPatches(new WURFLResources(patches));
    }
    
    /**
     * {@inheritDoc}
     */
    public void applyPatches(WURFLResources patches) {
        if (patches == null) {
            logger.warn("null patches, do nothing...");
            return;
        }

        readWriteLock.writeLock().lock();

        try {
            wurflService.applyPatches(patches, capabilityFilter);
        } finally {

            readWriteLock.writeLock().unlock();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void reload(String rootPath, String[] patchesPath) {
        WURFLResource root = createResource(rootPath);
        WURFLResources patches = createResources(patchesPath);

        reload(root, patches);
    }
    
    /**
     * {@inheritDoc}
     */
    public void reload(WURFLResource root, WURFLResource... patches) {
        reload(root, new WURFLResources(patches));
    }
    
    /**
     * {@inheritDoc}
     */
    public void reload(WURFLResource root, WURFLResources patches) {

    	initIfNeeded();
    	
        WURFLResources notNullPatches = createNotNullWURFLResources(patches);

        readWriteLock.writeLock().lock();

        try {
            wurflService.reload(root, notNullPatches, capabilityFilter);
        } finally {

            readWriteLock.writeLock().unlock();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void setMarkupResolver(MarkupResolver markupResolver) {
        this.markupResolver = markupResolver;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setCapabilitiesHolderFactory(CapabilitiesHolderFactory capabilitiesHolderFactory) {
    	this.capabilitiesHolderFactory = capabilitiesHolderFactory;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setWurflRequestFactory(WURFLRequestFactory wurflRequestFactory) {
        this.wurflRequestFactory = wurflRequestFactory;
        if (wurflRequestFactory instanceof WURFLRequestFactoryWithPriority) {
        	this.userAgentPriority = ((WURFLRequestFactoryWithPriority)wurflRequestFactory).getUserAgentPriority();
		}
    }
    
    /**
     * {@inheritDoc}
     */
    public void setUserAgentResolver(UserAgentResolver userAgentResolver) {
        this.userAgentResolver = userAgentResolver;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setDeviceProvider(DeviceProvider deviceProvider) {
        this.deviceProvider = deviceProvider;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setCacheProvider(CacheProvider cacheProvider) {
        this.cacheProvider = cacheProvider;
    }

    
    /**
     * {@inheritDoc}
     */
    public final WURFLUtils getWURFLUtils() {
        
    	initIfNeeded();
    	
        readWriteLock.readLock().lock();
        WURFLUtils returningUtils = wurflUtils;
        readWriteLock.readLock().unlock();

        return returningUtils;
        
    }

    /**
     * Checks is initialization is needed, and optionally performs it.
     * Synchronization is granted only when needed.
     */
    private void initIfNeeded() {
    	if (!engineInitilized) {
			synchronized (engineInitilized) {
				if (!engineInitilized) {
					try {
						init();
					} catch (Exception e) {
						logger.error("cannot initialize: " + e, e);
						throw new WURFLRuntimeException(e);
					}
				}
			}
		}
    }

    /**
     * Initializes the engine, exploiting provided customized objects, or creating new, default
     * ones if needed.
     */
    private void init() {
    	
    	try {

	        readWriteLock.writeLock().lock();
	
	        if (model == null) {
	    		model = new DefaultWURFLModel(root, patches, capabilityFilter);
	        }
	
	        root = null;
	        patches = null;
	
	        if (wurflService == null) {
	
	        	MatcherManager matcherManager = new MatcherManager(model);
	
	            if (markupResolver != null) {
	                if (logger.isInfoEnabled()) {
	                    logger.info("markupResolver is custom: " + markupResolver.getClass().getName());
	                }
	            }
	            if (capabilitiesHolderFactory != null) {
	                if (logger.isInfoEnabled()) {
	                    logger.info("capabilitiesHolderFactory is custom: " + capabilitiesHolderFactory.getClass().getName());
	                }
	            }
	            checkDeviceProvider();
	
	            if (cacheProvider != null) {
	                if (logger.isInfoEnabled()) {
	                    logger.info("cacheProvider is custom: " + cacheProvider.getClass().getName());
	                }
	            }
	            
	            WURFLRequestFactory wurflRequestFactory = this.wurflRequestFactory;
	            this.wurflRequestFactory = null;
	            if (wurflRequestFactory == null) {
	                if (userAgentResolver != null) {
	                	if (logger.isInfoEnabled()) {
	                		logger.info("userAgentResolver is custom: " + userAgentResolver.getClass().getName());
	                	}
	                	wurflRequestFactory = new DefaultWURFLRequestFactory(userAgentResolver, userAgentPriority);
	                } else {
	                	wurflRequestFactory = new DefaultWURFLRequestFactory(userAgentPriority);
	                }
	            } else {
	                if (logger.isInfoEnabled()) {
	                    logger.info("wurflRequestFactory is custom: " + wurflRequestFactory.getClass().getName());
	                }
	            }
                if (desiredEngineTarget != null) {
                	wurflService = new DefaultWURFLService(model, matcherManager, deviceProvider, wurflRequestFactory, desiredEngineTarget);
                } else {
                	wurflService = new DefaultWURFLService(model, matcherManager, deviceProvider, wurflRequestFactory);
                }
	
	        } else {
	            if (logger.isInfoEnabled()) {
	                logger.info("wurflService is fed: " + wurflService.getClass().getName());
	            }
	        }
	        checkDeviceProvider();
	
	        if (cacheProvider != null) {
	            if (logger.isInfoEnabled()) {
	                logger.info("cacheProvider is fed: " + cacheProvider.getClass().getName());
	            }
	            wurflService.setCacheProvider(cacheProvider);
	        }
	
	        if (wurflUtils == null) {
	        	wurflUtils = new WURFLUtils(model, deviceProvider);
	        }
	        
	        engineInitilized = true;
    	} finally {
    		readWriteLock.writeLock().unlock();
    	}
    }

    private void checkDeviceProvider() {
    	if (capabilitiesHolderFactory == null) {
    		capabilitiesHolderFactory = new DefaultCapabilitiesHolderFactory(model);
    	}
    	
    	// NOTICE: kept here instead of in DeviceProvider constructor for future customization
    	//         this needs to be re-initialized, since capabilitiesHolderFactory can change due to reload
    	VirtualCapabilitiesHolderFactory virtualCapabilitiesHolderFactory = new DefaultVirtualCapabilitiesHolderFactory(capabilitiesHolderFactory);

        if (deviceProvider == null) {
			if (markupResolver != null) {
				// full constructor
				deviceProvider = new DefaultDeviceProvider(model, capabilitiesHolderFactory, virtualCapabilitiesHolderFactory, markupResolver);
			} else {
				// default MarkupResolver
				deviceProvider = new DefaultDeviceProvider(model, capabilitiesHolderFactory, virtualCapabilitiesHolderFactory);
			}
        } else {
            if (logger.isInfoEnabled()) {
                logger.info("Device Provider is fed: " + deviceProvider.getClass().getName());
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public Device getDeviceForRequest(HttpServletRequest request) {
    	initIfNeeded();
        return wurflService.getDeviceForRequest(request);
    }
    
    /**
     * {@inheritDoc}
     */
    public Device getDeviceForRequest(WURFLRequest request) {
    	initIfNeeded();
    	return wurflService.getDeviceForRequest(request);
    }
    
    /**
     * {@inheritDoc}
     */
    public Device getDeviceForRequest(String userAgent) {
    	initIfNeeded();
    	return wurflService.getDeviceForRequest(userAgent);
    }
    
    /**
     * {@inheritDoc}
     */
	public EngineTarget getEngineTarget() {
		if (wurflService != null) {
			desiredEngineTarget = wurflService.getEngineTarget();
		}
		return desiredEngineTarget;
	}

    /**
     * {@inheritDoc}
     */
	public void setEngineTarget(EngineTarget target) {
		desiredEngineTarget = target;
		if (wurflService != null) {
			wurflService.setEngineTarget(target);
		}
	}
    
    /**
     * {@inheritDoc}
     */
	public UserAgentPriority getUserAgentPriority() {
		if (wurflService != null) {
			userAgentPriority = wurflService.getUserAgentPriority();
		} else if (wurflRequestFactory != null) {
			if (wurflRequestFactory instanceof WURFLRequestFactoryWithPriority) {
				return ((WURFLRequestFactoryWithPriority)wurflRequestFactory).getUserAgentPriority();
			} else {
				throwIfRequestFactoryHasNoPriority();
				return null;
			}
		}
		return userAgentPriority;
	}

    /**
     * {@inheritDoc}
     */
	public void setUserAgentPriority(UserAgentPriority userAgentPriority) {
		this.userAgentPriority = userAgentPriority;
		if (wurflService != null) {
			wurflService.setUserAgentPriority(userAgentPriority);
		}
		if (wurflRequestFactory != null) {
			if (wurflRequestFactory instanceof WURFLRequestFactoryWithPriority) {
				((WURFLRequestFactoryWithPriority)wurflRequestFactory).setUserAgentPriority(userAgentPriority);
			} else {
				throwIfRequestFactoryHasNoPriority();
			}
		}
	}
	
	private void throwIfRequestFactoryHasNoPriority() {
		throw new UnsupportedOperationException("User-Agent priority is not supported if the custom request factory does not implement WURFLRequestFactoryWithPriority");
	}
    
    /**
     * {@inheritDoc}
     */
    public final void setCapabilityFilter(String... capabilityFilter) {
    	this.capabilityFilter = capabilityFilter;
    }
    
    /**
     * {@inheritDoc}
     */ 
    public final void setCapabilityFilter(Collection<String> capabilityFilter) {
		if (capabilityFilter != null) {
			this.capabilityFilter = capabilityFilter.toArray(new String[0]);
		}
	}

	/**
     * Create XMLResource from path.
     *
     * @param path The path of resource to create, it must be not null.
     * @return XMLResource created from given path.
     */
    private WURFLResource createResource(String path) {

        Validate.notEmpty(path, "The path is null");

        WURFLResource resource = new XMLResource(path);

        return resource;
    }

    /**
     * Create a WURFLResources instance from paths array. The effective
     * WURFLResource created depends upon {@link #createResource(String)}.
     *
     * @param paths The paths array from which creating WURFLResources.
     * @return WURFLResources created from paths array. If the paths array is
     *         null or empty, it returns empty WURFLResources, not null.
     */
    private WURFLResources createResources(String[] paths) {
        WURFLResources resources = new WURFLResources();
        for (int index = 0; paths != null && index < paths.length; index++) {
            resources.add(new XMLResource(paths[index]));
        }

        return resources;
    }

    /**
     * Avoid problems with null patches, creating an empty WURFLResources
     * @param resources
     */
    private WURFLResources createNotNullWURFLResources(final WURFLResources resources) {
        WURFLResources notNullResources = resources;
        if (notNullResources == null) {
            notNullResources = new WURFLResources();
        }

        return notNullResources;
    }
	
}
