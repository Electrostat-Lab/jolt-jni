/*
Copyright (c) 2024 Stephen Gold

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package com.github.stephengold.joltjni;

/**
 * Perform simulation on a collection of physics objects. Bodies are added via a
 * separate interface.
 *
 * @author Stephen Gold sgold@sonic.net
 */
public class PhysicsSystem extends NonCopyable {
    // *************************************************************************
    // fields

    /**
     * interface for adding/modifying/querying/removing bodies (not null)
     */
    final private BodyInterface bodyInterface;
    /**
     * protect the BroadPhaseLayerInterface from garbage collection
     */
    private MapObj2Bp map;
    /**
     * protect the ObjectVsBroadPhaseLayerFilter from garbage collection
     */
    private ObjVsBpFilter ovbFilter;
    /**
     * protect the ObjectLayerPairFilter from getting garbage collection
     */
    private ObjVsObjFilter ovoFilter;
    // *************************************************************************
    // constructors

    /**
     * Instantiate an uninitialized physics system.
     */
    public PhysicsSystem() {
        long systemVa = createPhysicsSystem();
        setVirtualAddress(systemVa, true);

        long bodyInterfaceVa = getBodyInterface(systemVa);
        this.bodyInterface = new BodyInterface(bodyInterfaceVa);
    }
    // *************************************************************************
    // new methods exposed

    /**
     * Access the system's {@code BodyInterface}.
     *
     * @return the pre-existing JVM object (not null)
     */
    public BodyInterface getBodyInterface() {
        assert bodyInterface != null;
        return bodyInterface;
    }

    /**
     * Copy the gravity vector. The physics system is unaffected.
     *
     * @return a new acceleration vector (meters per second squared in
     * physics-system coordinates)
     */
    public Vec3 getGravity() {
        long systemVa = va();
        float x = getGravityX(systemVa);
        float y = getGravityY(systemVa);
        float z = getGravityZ(systemVa);
        Vec3 result = new Vec3(x, y, z);

        return result;
    }

    /**
     * Count how many active bodies of the specified type there are in the body
     * manager. The physics system is unaffected.
     *
     * @param bodyType which type of body to count (not null)
     * @return the count (&ge;0)
     */
    public int getNumActiveBodies(EBodyType bodyType) {
        long systemVa = va();
        int typeOrdinal = bodyType.ordinal();
        int result = getNumActiveBodies(systemVa, typeOrdinal);

        return result;
    }

    /**
     * Count how many bodies there are in the body manager. The physics system
     * is unaffected.
     *
     * @return the count (&ge;0)
     */
    public int getNumBodies() {
        long systemVa = va();
        int result = getNumBodies(systemVa);

        return result;
    }

    /**
     * Copy the system's settings. The physics system is unaffected.
     *
     * @return a new JVM object with a new native object assigned
     *
     * @see setPhysicsSettings
     */
    public PhysicsSettings getPhysicsSettings() {
        long systemVa = va();
        long settingsVa = getPhysicsSettings(systemVa);
        PhysicsSettings result = new PhysicsSettings(settingsVa, true);

        return result;
    }

    /**
     * Initialize the physics system with the specified limits.
     *
     * @param maxBodies the desired maximum number of rigid bodies that can be
     * added
     * @param numBodyMutexes the desired number of mutexes to allocate, or 0 for
     * the default number
     * @param maxBodyPairs the desired maximum number of body pairs that can be
     * queued at a time
     * @param maxContactConstraints the desired capacity of the
     * contact-constraint buffer
     * @param map (not null, alias created)
     * @param ovbFilter (not null, alias created)
     * @param ovoFilter (not null, alias created)
     */
    public void init(int maxBodies, int numBodyMutexes, int maxBodyPairs,
            int maxContactConstraints, MapObj2Bp map, ObjVsBpFilter ovbFilter,
            ObjVsObjFilter ovoFilter) {
        this.map = map;
        this.ovbFilter = ovbFilter;
        this.ovoFilter = ovoFilter;

        long systemVa = va();
        long mapVa = map.va();
        long ovbFilterVa = ovbFilter.va();
        long ovoFilterVa = ovoFilter.va();
        init(systemVa, maxBodies, numBodyMutexes, maxBodyPairs,
                maxContactConstraints, mapVa, ovbFilterVa, ovoFilterVa);
    }

    /**
     * Improve the performance of future collision detections.
     */
    public void optimizeBroadPhase() {
        optimizeBroadPhase(va());
    }

    /**
     * Alter the system's gravity vector.
     *
     * @param gravity (not null, unaffected)
     */
    public void setGravity(Vec3Arg gravity) {
        long systemVa = va();
        float x = gravity.getX();
        float y = gravity.getY();
        float z = gravity.getZ();
        setGravity(systemVa, x, y, z);
    }

    /**
     * Copy the specified settings to the system.
     *
     * @param settings the desired settings (not null, unaffected)
     *
     * @see getPhysicsSettings
     */
    public void setPhysicsSettings(PhysicsSettings settings) {
        long systemVa = va();
        long settingsVa = settings.va();
        setPhysicsSettings(systemVa, settingsVa);
    }

    /**
     * Advance the simulation by the specified amount.
     *
     * @param deltaTime the total time to advance (in seconds)
     * @param collisionSteps the number of simulation steps to perform
     * @param tempAllocator the allocator to use (not null)
     * @param jobSystem the job system to use (not null)
     * @return a bitmask of error conditions, or-ed together
     *
     * @see com.github.stephengold.joltjni.EPhysicsUpdateError
     */
    public int update(float deltaTime, int collisionSteps,
            TempAllocator tempAllocator, JobSystem jobSystem) {
        long physicsSystemVa = va();
        long allocatorVa = tempAllocator.va();
        long jobSystemVa = jobSystem.va();
        int result = update(physicsSystemVa, deltaTime, collisionSteps,
                allocatorVa, jobSystemVa);

        return result;
    }
    // *************************************************************************
    // native private methods

    native private static long createPhysicsSystem();

    native private static long getBodyInterface(long systemVa);

    native private static float getGravityX(long systemVa);

    native private static float getGravityY(long systemVa);

    native private static float getGravityZ(long systemVa);

    native private static int getNumActiveBodies(
            long systemVa, int typeOrdinal);

    native private static int getNumBodies(long systemVa);

    native private static long getPhysicsSettings(long systemVa);

    native private static void init(long systemVa, int maxBodies,
            int numBodyMutexes, int maxBodyPairs, int maxContactConstraints,
            long mapVa, long ovbFilterVa, long ovoFilterVa);

    native private static void optimizeBroadPhase(long systemVa);

    native private static void setGravity(
            long systemVa, float x, float y, float z);

    native private static void setPhysicsSettings(
            long systemVa, long settingsVa);

    native private static int update(long physicsSystemVa, float deltaTime,
            int collisionSteps, long allocatorVa, long jobSystemVa);
}
